package com.sprint.mission.discodeit.config;

public final class RepoProps {

  public static final String PREFIX = "discodeit.repository";
  public static final String TYPE_NAME = "type";
  public static final String TYPE_JCF = "jcf";
  public static final String TYPE_FILE = "file";
  public static final String FILE_DIRECTORY_KEY = "discodeit.repository.file-directory";
  public static final String FILE_DIRECTORY_DEFAULT = ".discodeit";
  public static final String FILE_DIRECTORY_PLACEHOLDER =
      "${" + FILE_DIRECTORY_KEY + ":" + FILE_DIRECTORY_DEFAULT + "}";

  private RepoProps() {
  }
}
