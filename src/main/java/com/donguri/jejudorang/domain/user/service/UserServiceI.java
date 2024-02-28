package com.donguri.jejudorang.domain.user.service;

import com.donguri.jejudorang.domain.bookmark.service.BookmarkService;
import com.donguri.jejudorang.domain.community.dto.response.CommunityMyPageListResponse;
import com.donguri.jejudorang.domain.community.service.CommunityService;
import com.donguri.jejudorang.domain.community.service.comment.CommentService;
import com.donguri.jejudorang.domain.notification.service.NotificationService;
import com.donguri.jejudorang.domain.user.dto.request.*;
import com.donguri.jejudorang.domain.user.dto.request.email.MailChangeRequest;
import com.donguri.jejudorang.domain.user.dto.request.email.MailSendForPwdRequest;
import com.donguri.jejudorang.domain.user.dto.request.email.MailSendRequest;
import com.donguri.jejudorang.domain.user.dto.request.email.MailVerifyRequest;
import com.donguri.jejudorang.domain.user.dto.response.ProfileResponse;
import com.donguri.jejudorang.domain.user.entity.*;
import com.donguri.jejudorang.domain.user.entity.auth.Password;
import com.donguri.jejudorang.domain.user.repository.RoleRepository;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import com.donguri.jejudorang.domain.user.service.auth.MailService;
import com.donguri.jejudorang.global.auth.jwt.JwtProvider;
import com.donguri.jejudorang.global.auth.jwt.JwtUserDetails;
import com.donguri.jejudorang.global.auth.jwt.RefreshToken;
import com.donguri.jejudorang.global.auth.jwt.RefreshTokenRepository;
import com.donguri.jejudorang.global.common.s3.ImageService;
import com.donguri.jejudorang.global.error.CustomErrorCode;
import com.donguri.jejudorang.global.error.CustomException;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

@Slf4j
@Service
public class UserServiceI implements UserService {

    @Autowired
    private final ImageService imageService;
    @Autowired
    private final MailService mailService;

    @Autowired
    private final CommunityService communityService;
    @Autowired
    private final BookmarkService bookmarkService;
    @Autowired
    private final CommentService commentService;
    @Autowired
    private final NotificationService notificationService;

    @Autowired
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final RoleRepository roleRepository;


    @Autowired
    private final PasswordEncoder encoder;
    @Autowired
    private final JwtProvider jwtProvider;


    @Value("${aws.s3.default-img.name}")
    private String defaultImgName;
    @Value("${aws.s3.default-img.url}")
    private String defaultImgUrl;


    public UserServiceI(ImageService imageService, MailService mailService, BookmarkService bookmarkService, CommentService commentService, NotificationService notificationService, AuthenticationManager authenticationManager, RefreshTokenRepository refreshTokenRepository, UserRepository userRepository, RoleRepository roleRepository, CommunityService communityService, PasswordEncoder encoder, JwtProvider jwtProvider) {
        this.imageService = imageService;
        this.mailService = mailService;
        this.bookmarkService = bookmarkService;
        this.commentService = commentService;
        this.notificationService = notificationService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.communityService = communityService;
        this.encoder = encoder;
        this.jwtProvider = jwtProvider;
    }


    /*
    * 이메일 확인 - 중복 확인 후 인증 번호 전송
    * > MailSendRequest
    *
    * */
    @Override
    @Transactional
    public void checkMailDuplicatedAndSendVerifyCode(MailSendRequest mailSendRequest) throws MessagingException {
        try {
            checkMailDuplicated(mailSendRequest.email());

            String subject = "[제주도랑] 회원 가입 인증번호입니다.";
            String code = createNumber();
            String mailBody = "<h3> 하단의 인증번호를 정확하게 입력해주세요.</h3>"
                    + "<p>인증번호: <b style='color:#FB7A51'>" + code + "</b></p><br><br>";
            mailService.sendAuthMail(mailSendRequest.email(), subject, mailBody, code);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    /*
    * 이메일 인증 번호 확인
    * > MailVerifyRequest
    *
    * */
    @Override
    @Transactional
    public boolean checkVerifyMail(MailVerifyRequest mailVerifyRequest) {
        try {
            return mailService.checkAuthMail(mailVerifyRequest);

        }  catch (CustomException e) {
            log.error("인증번호가 만료되었습니다.");
            throw e;

        } catch (Exception e) {
            log.error("인증번호 확인 실패 : {}",e.getMessage());
            throw e;
        }
    }

    // 이메일 중복 확인
    private void checkMailDuplicated(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            log.debug("이미 가입된 이메일입니다 : {}", email);
            throw new CustomException(CustomErrorCode.EMAIL_ALREADY_EXISTED);
        }
    }

    // 이메일 인증 번호 생성
    private static String createNumber() {
        int length = 6;

        try {
            Random random = SecureRandom.getInstanceStrong();

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();

        } catch (NoSuchAlgorithmException e) {
            log.debug("이메일 인증번호 생성을 실패했습니다 : {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


    /*
    * 회원 가입
    * > SignUpRequest
    *
    * */
    @Override
    @Transactional
    public void signUp(SignUpRequest signUpRequest) {
        if (userRepository.findByExternalId(signUpRequest.externalId()).isPresent()) {
            throw new CustomException(CustomErrorCode.ID_ALREADY_EXISTED);
        }

        if (userRepository.findByEmail(signUpRequest.emailToSend()).isPresent()) {
            throw new CustomException(CustomErrorCode.EMAIL_ALREADY_EXISTED);
        }

        if (!signUpRequest.password().equals(signUpRequest.passwordForCheck())) {
            throw new CustomException(CustomErrorCode.PASSWORD_MISMATCH);
        }

        /*
         * 새로운 회원(User) 생성
         * */
        try {
            User userToSave = signUpRequest.toEntity();
            userToSave.getProfile().updateImg(defaultImgName, defaultImgUrl);

            // set password
            Password pwdToSet = Password.builder()
                    .user(userToSave)
                    .build();
            pwdToSet.updatePassword(encoder, signUpRequest.password()); // encode pwd
            userToSave.updatePwd(pwdToSet);

            // set role
            Set<String> strRoles = signUpRequest.role();
            Set<Role> roles = new HashSet<>();

            if (strRoles == null) {
                Role userRole = roleRepository.findByName(ERole.USER)
                        .orElseThrow(() -> new CustomException(CustomErrorCode.ROLE_NOT_FOUND));
                roles.add(userRole);

            } else {
                strRoles.forEach(role -> {
                    if (role.equals("admin")) {
                        Role adminRole = roleRepository.findByName(ERole.ADMIN)
                                .orElseThrow(() -> new CustomException(CustomErrorCode.ROLE_NOT_FOUND));
                        roles.add(adminRole);
                    } else {
                        Role userRole = roleRepository.findByName(ERole.USER)
                                .orElseThrow(() -> new CustomException(CustomErrorCode.ROLE_NOT_FOUND));
                        roles.add(userRole);
                    }
                });
            }

            userToSave.updateRole(roles);

            userRepository.save(userToSave);

        } catch (Exception e) {
            log.error("회원 가입에 실패했습니다: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


    /*
    * 로그인
    * > LoginRequest
    *
    * */
    @Override
    @Transactional
    public Map<String, String> signIn(LoginRequest loginRequest) {
        log.info("[User Service] SIGN IN START ** ");

        try {
            // 로그인 아이디, 비밀번호 기반으로 유저 정보(JwtUserDetails) 찾아서 Authentication 리턴
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.externalId(), loginRequest.password()));
            log.info("[SignIn Service] 현재 로그인한 유저 인증 : {}", String.valueOf(authentication));

            // securityContext에 authentication 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("[SignIn Service] Security Context에 인증 정보 설정 : {}", String.valueOf(authentication));

            // 인증한 정보 기반으로 access token 생성
            String jwtAccess = jwtProvider.generateAccessToken(authentication);
            log.info("[SignIn Service] Access Token 생성 완료 : {}", jwtAccess);

            // refresh token
            String jwtRefresh = null;
            Optional<RefreshToken> refreshOp = refreshTokenRepository.findByUserId(authentication.getName());
            if (refreshOp.isPresent()) {
                log.info("Redis에 해당 아이디의 유효한 Refresh Token이 존재: {}", refreshOp);
                jwtRefresh = refreshOp.get().getRefreshToken();

            } else { // refresh token은 만료기간에 맞춰 redis에서 자동 삭제
                log.info("Redis에 해당 아이디의 Refresh Token이 없음: {}", authentication.getName());

                /*
                 * Refresh Token 생성 후 Redis에 저장
                 * */
                jwtRefresh = jwtProvider.generateRefreshTokenFromUserId(authentication);
                RefreshToken refreshTokenToSave = RefreshToken.builder()
                        .refreshToken(jwtRefresh)
                        .userId(authentication.getName())
                        .build();

                RefreshToken saved = refreshTokenRepository.save(refreshTokenToSave);
                log.info("Redis에 Refresh Token이 저장되었습니다 : {}", saved.getRefreshToken());
            }

            // 인증된 정보 기반 해당 사용자 세부 정보
            JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
            log.info("인증 정보 기반 사용자 세부 정보 : {}", userDetails.getUsername());

            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", jwtAccess);
            tokens.put("refresh_token", jwtRefresh);

            return tokens;

        } catch (BadCredentialsException e) {
            log.error("아이디/비밀번호가 올바르지 않습니다 : {}", e.getMessage());
            return null;

        } catch (Exception e) {
            log.error("인증에 실패했습니다 : {}", e.getMessage());
            return null;
        }

    }

    /*
     * SecurityConfig .logout() 설정으로 실행되지 않음
     * */
    @Override
    @Transactional
    public Optional<Authentication> logOut() {
        SecurityContextHolder.clearContext();
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    /*
    * 프로필 조회
    * > token
    * */
    @Override
    @Transactional
    public ProfileResponse getProfileData(String token) {
        try {
            User nowUser = getNowUser(token);
            return ProfileResponse.builder().build().from(nowUser);

        } catch (CustomException e) {
            log.error("Custom 프로필 조회 실패: {}", e.getCustomErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error("프로필 조회에 실패했습니다 : {}", e.getMessage());
            throw e;
        }
    }

    /*
    * 프로필 전체 수정
    * > token
    * > ProfileRequest
    *
    * */
    @Override
    @Transactional
    public void updateProfileData(String token, ProfileRequest dataToUpdate) {
        try {
            User nowUser = getNowUser(token);

            if (!dataToUpdate.img().isEmpty()) {
                if (dataToUpdate.img().getSize() > 1000000) {
                    throw new CustomException(CustomErrorCode.IMAGE_TOO_LARGE);
                }

                String pastImg = nowUser.getProfile().getImgName();
                if (pastImg != null) {
                    imageService.deleteImg(pastImg);

                    nowUser.getProfile().updateImg(defaultImgName, defaultImgUrl);

                    log.info("이전 이미지 삭제 완료");
                }

                Map<String, String> uploadedImg = imageService.uploadImg(dataToUpdate.img());

                if (uploadedImg == null) {
                    throw new CustomException(CustomErrorCode.FAILED_TO_UPLOAD_IMAGE);

                } else {
                    nowUser.getProfile().updateImg(uploadedImg.get("imgName"), uploadedImg.get("imgUrl"));

                    log.info("이미지 업로드 완료 : {}", uploadedImg.get("imgName"));
                }
            }

            Profile profile = nowUser.getProfile();
            profile.updateNickname(dataToUpdate.nickname());

            log.info("프로필 업데이트를 완료했습니다");

        } catch (Exception e) {
            log.error("프로필 업데이트에 실패했습니다 : {}", e.getMessage());
            throw e;
        }
    }

    /*
    * 프로필 사진만 삭제
    * > token
    *
    * */
    @Override
    @Transactional
    public void deleteProfileImg(String token) {
        try {
            User nowUser = getNowUser(token);

            String pastImg = nowUser.getProfile().getImgName();
            if (pastImg != null) {
                imageService.deleteImg(pastImg);

                nowUser.getProfile().updateImg(defaultImgName, defaultImgUrl);

                log.info("이전 이미지 삭제 완료");
            }
            log.info("프로필 업데이트를 완료했습니다");

        } catch (Exception e) {
            log.error("프로필 업데이트에 실패했습니다 : {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public String checkLoginType(String accessToken) {
        return getNowUser(accessToken).getLoginType().name();
    }


    /*
    * 비밀번호 변경
    * > token
    * > PasswordRequest
    *
    * */
    @Override
    @Transactional
    public void updatePassword(String token, PasswordRequest pwdToUpdate) throws Exception {
        try {
            User nowUser = getNowUser(token);

            if (!encoder.matches(pwdToUpdate.oldPwd(), nowUser.getPwd().getPassword())) {
                log.error("현재 비밀번호가 일치하지 않습니다.");
                throw new CustomException(CustomErrorCode.INVALID_PASSWORD);

            } else if (!pwdToUpdate.newPwd().equals(pwdToUpdate.newPwdToCheck())) {
                log.error("입력한 비밀번호가 일치하지 않습니다.");
                throw new CustomException(CustomErrorCode.PASSWORD_MISMATCH);

            } else {
                nowUser.getPwd().updatePassword(encoder, pwdToUpdate.newPwd());
                log.info("비밀번호 변경을 완료했습니다.");
            }

        } catch (Exception e) {
            log.error("패스워드 업데이트에 실패했습니다. {}", e.getMessage());
            throw e;
        }
    }

    /*
    * 이메일 변경
    * > token
    * > MailChangeRequest
    *
    * */
    @Override
    @Transactional
    public void updateEmail(String token, MailChangeRequest emailToUpdate) {

        try {
            User nowUser = getNowUser(token);

            nowUser.getAuth().updateEmail(emailToUpdate.emailToSend());
            log.info("이메일 변경이 완료되었습니다. 변경된 이메일: {}", nowUser.getAuth().getEmail());

        } catch (Exception e) {
            log.error("이메일 변경에 실패했습니다. {}", e.getMessage());
            throw e;
        }
    }


    /*
     * 아이디 찾기
     * : 아이디 전송
     * > MailSendRequest
     *
     * */
    @Override
    @Transactional
    public void sendMailWithId(MailSendRequest mailSendRequest) throws MessagingException {
        try {
            String foundId = userRepository.findByEmail(mailSendRequest.email())
                    .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND))
                    .getProfile().getExternalId();

            String subject = "[제주도랑] 아이디 찾기 결과입니다.";
            String mailBody = "<h3> 아이디 찾기 결과를 보내드립니다.</h3>"
                                + "<p>아이디: <b style='color:#FB7A51'>" + foundId + "</b></p><br><br>";
            mailService.sendMail(mailSendRequest.email(), subject, mailBody);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    /*
    * 비밀번호 찾기
    * : 인증번호 전송
    * > MailSendForPwdRequest
    *
    * */
    @Override
    @Transactional
    public void checkUserAndSendVerifyCode(MailSendForPwdRequest mailSendForPwdRequest) throws MessagingException {
        try {
            userRepository.findByEmailAndExternalId(mailSendForPwdRequest.email(), mailSendForPwdRequest.externalId())
                    .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

            String subject = "[제주도랑] 비밀번호 찾기 인증번호입니다.";
            String code = createNumber();
            String mailBody = "<h3> 하단의 인증번호를 정확하게 입력해주세요.</h3>"
                    + "<p>인증번호: <b style='color:#FB7A51'>" + code + "</b></p><br><br>";
            mailService.sendAuthMail(mailSendForPwdRequest.email(), subject, mailBody, code);

        } catch (Exception e) {
            log.error("인증번호 전송에 실패했습니다. {}", e.getMessage());
            throw e;
        }
    }

    /*
    * 랜덤 비밀번호 생성 & 비밀번호 변경 후 이메일 전송
    * > MailSendForPwdRequest
    *
    * */
    @Override
    @Transactional
    public void changePwdRandomlyAndSendMail(MailSendForPwdRequest mailSendForPwdRequest) throws MessagingException {
        try {
            User userToUpdatePwd = userRepository.findByEmailAndExternalId(mailSendForPwdRequest.email(), mailSendForPwdRequest.externalId())
                    .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

            // 임시 비밀번호 생성
            String randomPwd = createNumber();

            // 임시 비밀번호로 비밀번호 수정
            userToUpdatePwd.getPwd().updatePassword(encoder, randomPwd);

            String subject = "[제주도랑] 임시 비밀번호입니다.";
            String mailBody = "<h3>임시 비밀번호를 보내드립니다. 로그인하신 후 비밀번호를 재설정해주세요.</h3>"
                    + "<p>임시 비밀번호: <b style='color:#FB7A51'>" + randomPwd + "</b></p><br><br>";
            mailService.sendMail(mailSendForPwdRequest.email(), subject, mailBody);

        } catch (Exception e) {
            log.error("임시 비밀번호 생성에 실패했습니다. {}", e.getMessage());
            throw e;
        }
    }

    /*
    * 회원 탈퇴
    * > token
    *
    * */
    @Override
    @Transactional
    public void withdrawUser(String token) {
        try {
            Long idFromJwtToken = jwtProvider.getIdFromJwtToken(token);

            cutRelationshipAndDeleteAll(idFromJwtToken);

        } catch (Exception e) {
            log.error("회원을 삭제하지 못했습니다. {}", e.getMessage());
            throw e;
        }
    }

    private void cutRelationshipAndDeleteAll(Long idFromJwtToken) {
        // 추출한 아이디로 작성글 - 작성자 연관관계 삭제
        communityService.findAllPostsByUserAndSetWriterNull(idFromJwtToken);

        // 추출한 아이디로 작성 댓글 - 작성자 연관관계 삭제
        commentService.findAllCmtsByUserAndSetWriterNull(idFromJwtToken);

        // 알림 삭제
        notificationService.findAndDeleteAllNotificationsByUserId(idFromJwtToken);

        // 북마크 삭제
        bookmarkService.deleteAllBookmarksOfUser(idFromJwtToken);

        // 프로필 사진 삭제
        deleteProfileImage(idFromJwtToken);

        // 유저 삭제
        userRepository.deleteById(idFromJwtToken);
    }

    private void deleteProfileImage(Long idFromJwtToken) {
        String imgName = userRepository.findById(idFromJwtToken)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND))
                .getProfile().getImgName();

        // 기본 사진이 아닐 경우 삭제
        if (!imgName.equals("default-img.png")) {
            log.info("기본 사진이 아니므로 삭제합니다. 이름: {}", imgName);
            imageService.deleteImg(imgName);
        }
    }

    @Override
    @Transactional
    public void withdrawKakaoUser(String accessToken) {
        try {
            Long socialCodeFromJwt = jwtProvider.getIdFromJwtToken(accessToken);
            Long nowUserId = userRepository.findByLoginTypeAndSocialCode(LoginType.KAKAO, String.valueOf(socialCodeFromJwt))
                    .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND))
                    .getId();

            cutRelationshipAndDeleteAll(nowUserId);

        } catch (Exception e) {
            log.error("회원을 삭제하지 못했습니다. {}", e.getMessage());
            throw e;
        }
    }


    /*
    * 마이 페이지
    * 내 작성글 목록: 커뮤니티
    * > token
    * > pageable(nowPage)
    *
    * */
    @Override
    public Page<CommunityMyPageListResponse> getMyCommunityWritings(String token, Pageable pageable) {
        try {
            User nowUser = getNowUser(token);

            return communityService.getAllPostsWrittenByUser(nowUser, pageable);

        } catch (Exception e) {
            log.error("작성한 게시글 조회에 실패했습니다. {}", e.getMessage());
            throw e;
        }
    }

    /*
     * 마이 페이지
     * 내가 댓글단 글 목록: 커뮤니티
     * > token
     * > pageable(nowPage)
     *
     * */
    @Override
    @Transactional
    public Page<CommunityMyPageListResponse> getMyCommunityComments(String accessToken, Pageable pageable) {
        try {
            User nowUser = getNowUser(accessToken);

            return communityService.getAllPostsWithCommentsByUser(nowUser, pageable);

        } catch (Exception e) {
            log.error("댓글단 글 조회에 실패했습니다. {}", e.getMessage());
            throw e;
        }
    }

    /*
    * 마이 페이지
    * 내 북마크 목록: 여행/커뮤니티
    * > token
    * > type
    * > pageable(nowPage)
    *
    * */
    @Override
    public Page<?> getMyBookmarks(String token, String type, Pageable pageable) {
        try {
            User nowUser = getNowUser(token);

            return bookmarkService.getMyBookmarks(nowUser, type, pageable);

        } catch (Exception e) {
            log.error("북마크 불러오기에 실패했습니다. {}", e.getMessage());
            throw e;
        }
    }


    private User getNowUser(String token) {
        String userNameFromJwtToken = jwtProvider.getUserNameFromJwtToken(token);

        return userRepository.findByExternalId(userNameFromJwtToken)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
    }

}
