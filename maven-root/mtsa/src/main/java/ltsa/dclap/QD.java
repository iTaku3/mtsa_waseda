// dclap/quickdraw.java
// Macintosh QuickDraw info for PICT data
// d.gilbert, dec. 1996

package ltsa.dclap;

// pack edu.indiana.bio.dclap;

class QuickDrawFont {
  int val;
  String name;

  QuickDrawFont(int v, String n) {
    val = v;
    name = n;
  }

  int fontval(String n) {
    if (name.equalsIgnoreCase(n)) return val;
    else return -1;
  }
}

public class QD {
  // QuickDraw constants

  // font styles
  public static final int bold = 1;
  public static final int italic = 2;
  public static final int underline = 4;
  public static final int outline = 8;
  public static final int shadow = 16;
  public static final int condense = 32;
  public static final int extend = 64;

  // pen draw modes
  public static final int patCopy = 8;
  public static final int patOr = 9;
  public static final int patXor = 10;
  public static final int patBic = 11;
  public static final int hilite = 50;
  // also notPatXXX

  // MacDraw picComments
  public static final int picDwgBeg = 130;
  public static final int picDwgEnd = 131;
  public static final int picGrpBeg = 140;
  public static final int picGrpEnd = 141;
  public static final int textBegin = 150;
  public static final int textEnd = 151;
  public static final int textCenter = 154;
  // picComments for laserwriter
  public static final int dashedLine = 180;
  public static final int dashedStop = 181;
  public static final int setLineWidth = 182;

  public static final int version2 = 0x2ff;
  // enum PICTops // djang java for no enums !
  public static final int oNOP = 0;
  public static final int oClip = 1;
  public static final int oBkPat = 2;
  public static final int oTxFont = 3;
  public static final int oTxFace = 4;
  public static final int oTxMode = 5;
  public static final int oSpExtra = 6;
  public static final int oPnSize = 7;
  public static final int oPnMode = 8;
  public static final int oPnPat = 9;
  public static final int oFillPat = 10;
  public static final int oOvSize = 11;
  public static final int oOrigin = 12;
  public static final int oTxSize = 13;
  public static final int oFgColor = 14;
  public static final int oBkColor = 15;
  public static final int oTxRatio = 0x10;
  public static final int oVersion = 0x11;
  public static final int oBkPixPat = 0x12;
  public static final int oPnPixPat = 0x13;
  public static final int oFillPixPat = 0x14;
  public static final int oPnLocHFrac = 0x15;
  public static final int oChExtra = 0x16;
  // r17,r18,r19,
  public static final int oRGBFgCol = 0x1a;
  public static final int oRGBBkCol = 0x1b;
  public static final int oHiliteMode = 0x1c;
  public static final int oHiliteColor = 0x1d;
  public static final int oDefHilite = 0x1e;
  public static final int oOpColor = 0x1f;
  public static final int oLine = 0x20;
  public static final int oLineFrom = 0x21;
  public static final int oShortLine = 0x22;
  public static final int oShortLineFrom = 0x23;
  // r24,r25,r26,r27,
  public static final int oLongText = 0x28;
  public static final int oDHText = 0x29;
  public static final int oDVText = 0x2a;
  public static final int oDHDVText = 0x2b;
  public static final int oFontName = 0x2c;
  // r2d,
  // public final static int or2e_mov,
  // r2f,
  public static final int oframeRect = 0x30;
  public static final int opaintRect = 0x31;
  public static final int oeraseRect = 0x32;
  public static final int oinvertRect = 0x33;
  public static final int ofillRect = 0x34;
  // r35,r36,r37,
  public static final int oframeSameRect = 0x38;
  public static final int opaintSameRect = 0x39;
  public static final int oeraseSameRect = 0x3a;
  public static final int oinvertSameRect = 0x3b;
  public static final int ofillSameRect = 0x3c;
  // r3d,r3e,r3f,
  public static final int oframeRRect = 0x40;
  public static final int opaintRRect = 0x41;
  public static final int oeraseRRect = 0x42;
  public static final int oinvertRRect = 0x43;
  public static final int ofillRRect = 0x44;
  // r45,r46,r47,
  public static final int oframeSameRRect = 0x48;
  public static final int opaintSameRRect = 0x49;
  public static final int oeraseSameRRect = 0x4a;
  public static final int oinvertSameRRect = 0x4b;
  public static final int ofillSameRRect = 0x4c;
  // r4d,r4e,r4f,
  public static final int oframeOval = 0x50;
  public static final int opaintOval = 0x51;
  public static final int oeraseOval = 0x52;
  public static final int oinvertOval = 0x53;
  public static final int ofillOval = 0x54;
  // r55,r56,r57,
  public static final int oframeSameOval = 0x58;
  public static final int opaintSameOval = 0x59;
  public static final int oeraseSameOval = 0x5a;
  public static final int oinvertSameOval = 0x5b;
  public static final int ofillSameOval = 0x5c;
  // r5d,r5e,r5f,
  public static final int oframeArc = 0x60;
  public static final int opaintArc = 0x61;
  public static final int oeraseArc = 0x62;
  public static final int oinvertArc = 0x63;
  public static final int ofillArc = 0x64;
  // r65,r66,r67,
  public static final int oframeSameArc = 0x68;
  public static final int opaintSameArc = 0x69;
  public static final int oeraseSameArc = 0x6a;
  public static final int oinvertSameArc = 0x6b;
  public static final int ofillSameArc = 0x6c;
  // r6d,r6e,r6f,
  public static final int oframePoly = 0x70;
  public static final int opaintPoly = 0x71;
  public static final int oerasePoly = 0x72;
  public static final int oinvertPoly = 0x73;
  public static final int ofillPoly = 0x74;
  // r75,r76,r77,
  public static final int oframeSamePoly = 0x78;
  public static final int opaintSamePoly = 0x79;
  public static final int oeraseSamePoly = 0x7a;
  public static final int oinvertSamePoly = 0x7b;
  public static final int ofillSamePoly = 0x7c;
  // r7d,r7e,r7f,
  public static final int oframeRgn = 0x80;
  public static final int opaintRgn = 0x81;
  public static final int oeraseRgn = 0x82;
  public static final int oinvertRgn = 0x83;
  public static final int ofillRgn = 0x84;
  // r85,r86,r87,
  public static final int oframeSameRgn = 0x88;
  public static final int opaintSameRgn = 0x89;
  public static final int oeraseSameRgn = 0x8a;
  public static final int oinvertSameRgn = 0x8b;
  public static final int ofillSameRgn = 0x8c;
  // r8d,r8e,r8f,
  public static final int oBitsRect = 0x90;
  public static final int oBitsRgn = 0x91;
  // r92,r93,r94,r95,r96,r97,
  public static final int oPackBitsRect = 0x98;
  public static final int oPackBitsRgn = 0x99;
  public static final int oOpcode9A = 0x9a;
  // r9b,r9c,r9d,r9e,r9f,
  public static final int oShortComment = 0xa0;
  public static final int oLongComment = 0xa1;
  // a2..af,b0..fe unused
  public static final int oopEndPic = 0x00ff;
  // 0100..ffff unused
  public static final int oHeaderOp = 0x0c00;

  public static int fontnum = 101; // value past QuickDrawFont values?

  protected static QuickDrawFont[] QDFonts;

  static {
    int i = 0;
    QDFonts = new QuickDrawFont[18]; // ! can't let compiler count?!
    QDFonts[i++] = new QuickDrawFont(0, "Chicago"); // system
    QDFonts[i++] = new QuickDrawFont(1, "Geneva"); // applic
    QDFonts[i++] = new QuickDrawFont(2, "New York");
    QDFonts[i++] = new QuickDrawFont(3, "Geneva");
    QDFonts[i++] = new QuickDrawFont(4, "Monaco");
    QDFonts[i++] = new QuickDrawFont(13, "Zapf Dingbats");
    QDFonts[i++] = new QuickDrawFont(14, "Bookman");
    QDFonts[i++] = new QuickDrawFont(16, "Palatino");
    QDFonts[i++] = new QuickDrawFont(18, "Zapf Chancery");
    QDFonts[i++] = new QuickDrawFont(19, "Souvenir");
    QDFonts[i++] = new QuickDrawFont(20, "Times");
    QDFonts[i++] = new QuickDrawFont(21, "Helvetica");
    QDFonts[i++] = new QuickDrawFont(22, "Courier");
    QDFonts[i++] = new QuickDrawFont(23, "Symbol");
    QDFonts[i++] = new QuickDrawFont(26, "Lubalin Graph");
    QDFonts[i++] = new QuickDrawFont(33, "Avant Garde");
    QDFonts[i++] = new QuickDrawFont(21, "SansSerif");
    QDFonts[i++] = new QuickDrawFont(20, "Serif");
  }

  public static int getQuickDrawFontNum(String name) {
    for (int i = 0; i < QDFonts.length; i++) {
      int num = QDFonts[i].fontval(name);
      if (num >= 0) return num;
    }
    return -1;
  }
}
