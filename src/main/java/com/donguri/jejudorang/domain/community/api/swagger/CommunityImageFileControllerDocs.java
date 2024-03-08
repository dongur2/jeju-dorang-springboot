package com.donguri.jejudorang.domain.community.api.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Community: image", description = "커뮤니티(community) 게시글 이미지 관련 API")
public interface CommunityImageFileControllerDocs {

    @Operation(summary = "게시글에 포함된 이미지를 AWS S3 버킷에 업로드합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이미지 업로드 성공"),
            @ApiResponse(responseCode = "400", description = "이미지 업로드 실패: 첨부된 이미지 없음"),
            @ApiResponse(responseCode = "413", description = "이미지 업로드 실패: 첨부된 이미지 파일 용량 제한 초과"),
            @ApiResponse(responseCode = "415", description = "이미지 업로드 실패: 첨부된 이미지 파일 형식 미지원"),
    })
    String uploadEditorImage(@RequestParam("image") final MultipartFile image);

}
