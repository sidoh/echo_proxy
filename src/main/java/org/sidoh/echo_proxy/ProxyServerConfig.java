package org.sidoh.echo_proxy;

import java.util.Set;

public final class ProxyServerConfig {
  public final String endpoint;
  public final int port;
  public final Set<String> allowedApplications;
  public final Set<String> allowedUsers;
  public final boolean verifySignatures;
  public final boolean verifyTimestamps;

  public ProxyServerConfig() {
    this.endpoint = "";
    this.port = 8888;
    this.allowedApplications = null;
    this.allowedUsers = null;
    this.verifySignatures = true;
    this.verifyTimestamps = true;
  }

  public boolean allowApplication(String application) {
    return allowedApplications == null || allowedApplications.contains(application);
  }

  public boolean allowUser(String user) {
    return allowedUsers == null || allowedUsers.contains(user);
  }
}
