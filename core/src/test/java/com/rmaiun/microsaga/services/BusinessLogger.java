package com.rmaiun.microsaga.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BusinessLogger {

  private static final Logger LOG = LogManager.getLogger(BusinessLogger.class);


  public void createBusinessLog(String msg) {
    LOG.info(msg);
  }
}
