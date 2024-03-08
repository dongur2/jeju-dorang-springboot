package com.donguri.jejudorang.domain.user.dto.request;

import com.donguri.jejudorang.domain.user.entity.*;
import com.donguri.jejudorang.domain.user.entity.auth.Authentication;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import org.apache.coyote.BadRequestException;

import java.util.Set;

@Builder
@Schema(description = "회원가입 요청 정보")
public record SignUpRequest (

        @Schema(description = "아이디 (5자 이상 20자 이하의 영문자와 숫자 조합)", example = "testuser1", requiredMode = Schema.RequiredMode.REQUIRED)
        @Pattern(regexp=("^[a-zA-Z0-9]{5,20}$")
                ,message="아이디는 5자 이상 20자 이하의 영문자와 숫자 조합만 가능합니다.")
        @NotBlank(message = "아이디를 입력해주세요.")
        @Size(min = 5, max = 20, message = "아이디는 5자 이상 30자 이하만 가능합니다.")
        String externalId,

        @Schema(description = "닉네임 (2자 이상 15자 이하의 특수문자와 이모티콘을 제외한 문자열)", example = "테스트유저", requiredMode = Schema.RequiredMode.REQUIRED)
        @Pattern(regexp = ("^[a-zA-Z0-9가-힣]{2,15}$")
                ,message = "닉네임은 공백을 포함하는 특수문자, 이모티콘을 제외한 2자 이상 15자 이하만 가능합니다.")
        @NotBlank(message = "닉네임을 입력해주세요.")
        @Size(min = 2, max = 15, message = "닉네임은 2자 이상 15자 이하만 가능합니다.")
        String nickname,

        @Schema(description = "이메일 주소", example = "usertest@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @Email(message = "이메일 형식으로 작성해주세요.")
        @NotBlank(message = "이메일을 입력해주세요.")
        String emailToSend,

        @Schema(description = "이메일 인증 여부", example = "true", defaultValue = "false", requiredMode = Schema.RequiredMode.REQUIRED)
        @AssertTrue(message = "이메일 인증이 필요합니다.")
        boolean isVerified,

        @Schema(description = "제주도랑 이용약관 동의 여부 (필수 동의)", example = "true", defaultValue = "false", requiredMode = Schema.RequiredMode.REQUIRED)
        @AssertTrue(message = "제주도랑 이용약관에 동의해야 가입이 가능합니다.")
        boolean agreeForUsage,

        @Schema(description = "필수 개인정보 수집 및 이용에 동의 여부 (필수 동의)", example = "true", defaultValue = "false", requiredMode = Schema.RequiredMode.REQUIRED)
        @AssertTrue(message = "필수 개인정보 수집 및 이용에 동의해야 가입이 가능합니다.")
        boolean agreeForPrivateNecessary,

        @Schema(description = "선택 개인정보 수집 및 이용에 동의 여부", example = "false", defaultValue = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        boolean agreeForPrivateOptional,

        @Schema(description = "비밀번호 (8자 이상 20자 이하의 특수문자와 숫자가 적어도 하나가 포함된 문자열)", example = "testuser~11111", requiredMode = Schema.RequiredMode.REQUIRED)
        @Pattern(regexp=("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$")
                ,message="비밀번호는 특수문자와 숫자가 적어도 하나가 포함된 8자 이상 20자 이하만 가능합니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하만 가능합니다.")
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password,

        @Schema(description = "비밀번호 확인", example = "testuser~11111", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "비밀번호를 한 번 더 입력해주세요.")
        String passwordForCheck,

        @Schema(description = "권한 ('null'일 경우 USER, 'admin'일 경우 ADMIN)", example = "null", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Set<String> role

){
        public User toEntity() throws BadRequestException {
                User user = User.builder()
                        .loginType(LoginType.BASIC) // temp
                        .build();

                Profile prof = Profile.builder()
                        .user(user)
                        .externalId(externalId)
                        .nickname(nickname)
                        .build();

                Authentication auth = Authentication.builder()
                        .user(user)
                        .agreement(setAgreeRangeFromRequest())
                        .email(emailToSend)
                        .build();

                user.updateProfile(prof);
                user.updateAuth(auth);

                return user;
        }

        // 약관 동의 항목 범위 설정해 리턴
        private AgreeRange setAgreeRangeFromRequest() throws BadRequestException {
                if (!agreeForUsage || !agreeForPrivateNecessary) {
                        throw new RuntimeException("필수 동의 항목에 동의해야 가입이 가능합니다.");

                } else if (agreeForPrivateOptional == agreeForPrivateNecessary == agreeForUsage) {
                        return AgreeRange.ALL;

                } else {
                        return AgreeRange.NECESSARY;
                }
        }

}
