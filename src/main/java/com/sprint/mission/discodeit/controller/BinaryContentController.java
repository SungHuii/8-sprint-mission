package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent", description = "바이너리 컨텐츠(파일) 관리 API")
@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  @Operation(summary = "파일 단건 조회", description = "ID로 파일(이미지, 텍스트 파일 등) 데이터를 조회합니다. JSON 형태로 반환되며, bytes 필드에 Base64 인코딩된 데이터가 포함됩니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공"),
      @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음", content = @Content)
  })
  @GetMapping("/{binaryContentId}")
  public ResponseEntity<BinaryContent> findById(@PathVariable UUID binaryContentId) {
    return ResponseEntity.ok(binaryContentService.findById(binaryContentId));
  }

  @Operation(summary = "파일 목록 조회", description = "여러 ID에 해당하는 파일들의 메타데이터를 조회합니다.")
  @ApiResponse(responseCode = "200", description = "조회 성공")
  @GetMapping
  public ResponseEntity<List<BinaryContent>> findAllByIdIn(
      @RequestParam List<UUID> binaryContentIds) {
    return ResponseEntity.ok(binaryContentService.findAllByIdIn(binaryContentIds));
  }
}
