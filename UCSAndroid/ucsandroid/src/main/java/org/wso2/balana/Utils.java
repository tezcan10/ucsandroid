package org.wso2.balana;

public class Utils
{
  public static String prepareXPathForDefaultNs(String xpath)
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("/");
    String[] splitArray = xpath.split("/");
    String[] arrayOfString1;
    int j = (arrayOfString1 = splitArray).length;
    for (int i = 0; i < j; i++)
    {
      String s = arrayOfString1[i];
      if ((s != null) && (s.trim().length() > 0)) {
        buffer.append("/ns:").append(s);
      }
    }
    return buffer.toString();
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.Utils
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */