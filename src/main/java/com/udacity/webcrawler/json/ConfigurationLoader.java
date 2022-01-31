package com.udacity.webcrawler.json;

import java.io.Reader;
import java.io.*;
import java.nio.file.Path;
import java.util.Objects;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonParser.Feature;

/**
 * A static utility class that loads a JSON configuration file.
 */
public final class ConfigurationLoader {

  private final Path path;

  /**
   * Create a {@link ConfigurationLoader} that loads configuration from the given {@link Path}.
   */
  public ConfigurationLoader(Path path) {
    this.path = Objects.requireNonNull(path);
  }

  /**
   * Loads configuration from this {@link ConfigurationLoader}'s path
   *
   * @return the loaded {@link CrawlerConfiguration}.
   */
  public CrawlerConfiguration load() {
    // TODO: Fill in this method.
    CrawlerConfiguration crawlerConfiguration=null;
    try (Reader reader = Files.newBufferedReader(path)) {
      crawlerConfiguration= read(reader);
    } catch (IOException ex){

    }
    return crawlerConfiguration;
  }

  /**
   * Loads crawler configuration from the given reader.
   *
   * @param reader a Reader pointing to a JSON string that contains crawler configuration.
   * @return a crawler configuration
   */
  public static CrawlerConfiguration read(Reader reader) {
    // This is here to get rid of the unused variable warning.
    Objects.requireNonNull(reader);
    // TODO: Fill in this method

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.disable(Feature.AUTO_CLOSE_SOURCE);
    CrawlerConfiguration crawlerConfiguration=null;
    try {
      crawlerConfiguration=objectMapper.readValue(reader, CrawlerConfiguration.Builder.class).build();
    }catch (IOException ex){
    }
    return crawlerConfiguration;
  }
}
