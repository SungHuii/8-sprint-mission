package com.sprint.mission.discodeit.controller.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.MDCLoggingInterceptor;
import com.sprint.mission.discodeit.controller.AuthController;
import com.sprint.mission.discodeit.controller.BinaryContentController;
import com.sprint.mission.discodeit.controller.ChannelController;
import com.sprint.mission.discodeit.controller.MessageController;
import com.sprint.mission.discodeit.controller.ReadStatusController;
import com.sprint.mission.discodeit.controller.UserController;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
    AuthController.class,
    UserController.class,
    ChannelController.class,
    MessageController.class,
    ReadStatusController.class,
    BinaryContentController.class
})
@Import(MDCLoggingInterceptor.class)
public abstract class ControllerTestSupport {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @MockitoBean
  protected AuthService authService;

  @MockitoBean
  protected UserService userService;

  @MockitoBean
  protected UserStatusService userStatusService;

  @MockitoBean
  protected ChannelService channelService;

  @MockitoBean
  protected MessageService messageService;

  @MockitoBean
  protected ReadStatusService readStatusService;

  @MockitoBean
  protected BinaryContentService binaryContentService;

  @MockitoBean
  protected BinaryContentStorage binaryContentStorage;

  /* Mapper 부분 */
  @MockitoBean
  protected UserMapper userMapper;

  @MockitoBean
  protected ChannelMapper channelMapper;

  @MockitoBean
  protected MessageMapper messageMapper;

  @MockitoBean
  protected UserStatusMapper userStatusMapper;

  @MockitoBean
  protected ReadStatusMapper readStatusMapper;

  @MockitoBean
  protected BinaryContentMapper binaryContentMapper;
}
