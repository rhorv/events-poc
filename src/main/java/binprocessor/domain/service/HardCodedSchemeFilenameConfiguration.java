package binprocessor.domain.service;

import binprocessor.domain.IClassifyFilesToScheme;

public class HardCodedSchemeFilenameConfiguration implements IClassifyFilesToScheme {

  public String getSchemeNameFor(String filename) throws Exception {
    if (filename.indexOf("visa") != -1) {
      return "visa";
    }
    if (filename.indexOf("mastercard") != -1) {
      return "mastercard";
    }
    throw new Exception();
  }
}
