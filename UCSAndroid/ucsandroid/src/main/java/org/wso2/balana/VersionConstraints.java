package org.wso2.balana;

import java.util.StringTokenizer;

public class VersionConstraints
{
  private static final int COMPARE_EQUAL = 0;
  private static final int COMPARE_LESS = 1;
  private static final int COMPARE_GREATER = 2;
  private String version;
  private String earliest;
  private String latest;
  
  public VersionConstraints(String version, String earliest, String latest)
  {
    this.version = version;
    this.earliest = earliest;
    this.latest = latest;
  }
  
  public String getVersionConstraint()
  {
    return version;
  }
  
  public String getEarliestConstraint()
  {
    return earliest;
  }
  
  public String getLatestConstraint()
  {
    return latest;
  }
  
  public boolean meetsConstraint(String version)
  {
    return (matches(version, this.version)) && (isEarlier(version, latest)) && 
      (isLater(version, earliest));
  }
  
  public static boolean matches(String version, String constraint)
  {
    return compareHelper(version, constraint, 0);
  }
  
  public static boolean isEarlier(String version, String constraint)
  {
    return compareHelper(version, constraint, 1);
  }
  
  public static boolean isLater(String version, String constraint)
  {
    return compareHelper(version, constraint, 2);
  }
  
  private static boolean compareHelper(String version, String constraint, int type)
  {
    if (constraint == null) {
      return true;
    }
    if (version == null) {
      return true;
    }
    StringTokenizer vtok = new StringTokenizer(version, ".");
    StringTokenizer ctok = new StringTokenizer(constraint, ".");
    while (vtok.hasMoreTokens())
    {
      if (!ctok.hasMoreTokens())
      {
        if (type == 2) {
          return true;
        }
        return false;
      }
      String c = ctok.nextToken();
      if (c.equals("+")) {
        return true;
      }
      String v = vtok.nextToken();
      if (!c.equals("*")) {
        if (!v.equals(c))
        {
          if (type == 0) {
            return false;
          }
          int cint = Integer.valueOf(c).intValue();
          int vint = Integer.valueOf(v).intValue();
          if (type == 1) {
            return vint <= cint;
          }
          return vint >= cint;
        }
      }
    }
    if (ctok.hasMoreTokens())
    {
      if (type == 1) {
        return true;
      }
      return false;
    }
    return true;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.VersionConstraints
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */