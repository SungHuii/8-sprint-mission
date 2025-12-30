package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContent")
@RequiredArgsConstructor
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  @RequestMapping(value = "/find", method = RequestMethod.GET)
  public ResponseEntity<BinaryContent> findById(@RequestParam UUID binaryContentId) {
    return ResponseEntity.ok(binaryContentService.findById(binaryContentId));
  }

  @RequestMapping(value = "/findAll", method = RequestMethod.GET)
  public ResponseEntity<List<BinaryContent>> findAllByIdIn(
      @RequestParam List<UUID> binaryContentIds) {
    return ResponseEntity.ok(binaryContentService.findAllByIdIn(binaryContentIds));
  }
}
