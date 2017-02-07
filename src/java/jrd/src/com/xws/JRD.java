package com.xws;

import static java.lang.System.out;

public final class JRD {
  public static final void main(String[] args) throws Exception {
    out.println("Redis Demo...");

    //Simple.run();
    //Packer.run();
    //Connector.run();
    //Pipelines.run();
    //Transactions.run();
    //Keyspaces.run();
    Sentinels.run();
  }
}
