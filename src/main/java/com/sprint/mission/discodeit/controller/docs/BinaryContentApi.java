package com.sprint.mission.discodeit.controller.docs;

import com.sprint.mission.discodeit.dto.binary.BinaryContentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent", description = "바이너리 컨텐츠(파일) 관리 API")
public interface BinaryContentApi {

  @Operation(summary = "파일 단건 조회", description = "ID로 파일의 메타데이터를 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공"),
      @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음", content = @Content)
  })
  ResponseEntity<BinaryContentResponse> findById(@PathVariable UUID binaryContentId);

  @Operation(summary = "파일 목록 조회", description = "여러 ID에 해당하는 파일들의 메타데이터를 조회합니다.")
  @ApiResponse(responseCode = "200", description = "조회 성공")
  ResponseEntity<List<BinaryContentResponse>> findAllByIdIn(@RequestParam List<UUID> binaryContentIds);

  @Operation(summary = "파일 다운로드", description = "ID로 파일을 다운로드합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "다운로드 성공"),
      @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음", content = @Content)
  })
  ResponseEntity<?> download(@PathVariable UUID binaryContentId);
}
