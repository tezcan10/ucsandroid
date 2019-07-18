package org.wso2.balana.attr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

class Base64
{
  private static final char SPACE = ' ';
  private static final char ETX = '\004';
  private static final char VTAB = '\013';
  private static final char FF = '\f';
  private static final char HTAB = '\t';
  private static final char LF = '\n';
  private static final char ALTLF = '\023';
  private static final char CR = '\r';
  private static char PAD = '=';
  private static final String BASE64EncodingString = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/= \004\013\f\t\n\023\r";
  private static final int PAD_INDEX = 64;
  private static final int MAX_BASE64_CHAR = 122;
  private static int[] Base64DecodeArray = null;
  private static final int NO_CHARS_DECODED = 0;
  private static final int ONE_CHAR_DECODED = 1;
  private static final int TWO_CHARS_DECODED = 2;
  private static final int THREE_CHARS_DECODED = 3;
  private static final int PAD_THREE_READ = 5;
  private static final int PAD_FOUR_READ = 6;
  private static final int MAX_GROUPS_PER_LINE = 19;
  
  public static String encode(byte[] binaryValue)
  {
    int binaryValueLen = binaryValue.length;
    
    int maxChars = binaryValueLen * 7 / 5;
    
    StringBuffer sb = new StringBuffer(maxChars);
    
    int groupsToEOL = 19;
    for (int binaryValueNdx = 0; binaryValueNdx < binaryValueLen; binaryValueNdx += 3)
    {
      int group1 = binaryValue[binaryValueNdx] >> 2 & 0x3F;
      sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/= \004\013\f\t\n\023\r".charAt(group1));
      
      int group2 = binaryValue[binaryValueNdx] << 4 & 0x30;
      if (binaryValueNdx + 1 < binaryValueLen) {
        group2 |= binaryValue[(binaryValueNdx + 1)] >> 4 & 0xF;
      }
      sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/= \004\013\f\t\n\023\r".charAt(group2));
      int group3;
      if (binaryValueNdx + 1 < binaryValueLen)
      {
        int group3 = binaryValue[(binaryValueNdx + 1)] << 2 & 0x3C;
        if (binaryValueNdx + 2 < binaryValueLen) {
          group3 |= binaryValue[(binaryValueNdx + 2)] >> 6 & 0x3;
        }
      }
      else
      {
        group3 = 64;
      }
      sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/= \004\013\f\t\n\023\r".charAt(group3));
      int group4;
      int group4;
      if (binaryValueNdx + 2 < binaryValueLen) {
        group4 = binaryValue[(binaryValueNdx + 2)] & 0x3F;
      } else {
        group4 = 64;
      }
      sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/= \004\013\f\t\n\023\r".charAt(group4));
      
      groupsToEOL--;
      if (groupsToEOL == 0)
      {
        groupsToEOL = 19;
        if (binaryValueNdx + 3 <= binaryValueLen)
        {
          sb.append('\r');
          sb.append('\n');
        }
      }
    }
    return sb.toString();
  }
  
  private static void initDecodeArray()
  {
    if (Base64DecodeArray != null) {
      return;
    }
    int[] ourArray = new int[123];
    for (int i = 0; i <= 122; i++) {
      ourArray[i] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/= \004\013\f\t\n\023\r".indexOf(i);
    }
    Base64DecodeArray = ourArray;
  }
  
  public static byte[] decode(String encoded, boolean ignoreBadChars)
    throws IOException
  {
    int encodedLen = encoded.length();
    int maxBytes = encodedLen / 4 * 3;
    ByteArrayOutputStream ba = 
      new ByteArrayOutputStream(maxBytes);
    byte[] quantum = new byte[3];
    
    initDecodeArray();
    
    int state = 0;
    for (int encodedNdx = 0; encodedNdx < encodedLen; encodedNdx++)
    {
      int encodedChar = encoded.charAt(encodedNdx);
      int decodedChar;
      int decodedChar;
      if (encodedChar > 122) {
        decodedChar = -1;
      } else {
        decodedChar = Base64DecodeArray[encodedChar];
      }
      if (decodedChar == -1)
      {
        if (!ignoreBadChars) {
          throw new IOException("Invalid character");
        }
      }
      else if (decodedChar <= 64) {
        switch (state)
        {
        case 0: 
          if (decodedChar == 64) {
            throw new IOException("Pad character in invalid position");
          }
          quantum[0] = ((byte)(decodedChar << 2 & 0xFC));
          state = 1;
          break;
        case 1: 
          if (decodedChar == 64) {
            throw new IOException("Pad character in invalid position");
          }
          quantum[0] = ((byte)(quantum[0] | decodedChar >> 4 & 0x3));
          quantum[1] = ((byte)(decodedChar << 4 & 0xF0));
          state = 2;
          break;
        case 2: 
          if (decodedChar == 64)
          {
            ba.write(quantum, 0, 1);
            state = 5;
          }
          else
          {
            quantum[1] = ((byte)(quantum[1] | decodedChar >> 2 & 0xF));
            quantum[2] = ((byte)(decodedChar << 6 & 0xC0));
            state = 3;
          }
          break;
        case 3: 
          if (decodedChar == 64)
          {
            ba.write(quantum, 0, 2);
            state = 6;
          }
          else
          {
            quantum[2] = ((byte)(quantum[2] | decodedChar));
            ba.write(quantum, 0, 3);
            state = 0;
          }
          break;
        case 5: 
          if (decodedChar != 64) {
            throw new IOException("Missing pad character");
          }
          state = 6;
          break;
        case 6: 
          throw new IOException("Invalid input follows pad character");
        }
      }
    }
    if ((state != 0) && (state != 6)) {
      throw new IOException("Invalid sequence of input characters");
    }
    return ba.toByteArray();
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.Base64
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */