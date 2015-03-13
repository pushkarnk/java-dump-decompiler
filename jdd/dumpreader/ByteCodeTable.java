package jdd.dumpreader;

import java.util.regex.Pattern;


public class ByteCodeTable {
	
	/*Broad categories of bytecode operands */
	
	static final int NO_OPERATOR 		   = 0; //should have been NO_OPERAND, correction coming up in v2
	static final int VALUE       		   = 1;
	static final int CP_INDEX              = 2;
	static final int LV_INDEX              = 3;
	static final int LV_INDEX_AND_CONSTANT = 4;
	static final int BRANCH_OFFSET		   = 5;
	static final int INVOKE_INTERFACE      = 6;
	static final int ARRAY_TYPE            = 7;
	static final int MULTINEWARRAY	       = 8;
	static final int WIDE		           = 9;

	private static String [] byteCode = 
	{
		"JBnop" /* 0 */,
		"JBaconstnull" /* 1 */,
		"JBiconstm1" /* 2 */,
		"JBiconst0" /* 3 */,
		"JBiconst1" /* 4 */,
		"JBiconst2" /* 5 */,
		"JBiconst3" /* 6 */,
		"JBiconst4" /* 7 */,
		"JBiconst5" /* 8 */,
		"JBlconst0" /* 9 */,
		"JBlconst1" /* 10 */,
		"JBfconst0" /* 11 */,
		"JBfconst1" /* 12 */,
		"JBfconst2" /* 13 */,
		"JBdconst0" /* 14 */,
		"JBdconst1" /* 15 */,
		"JBbipush" /* 16 */,
		"JBsipush" /* 17 */,
		"JBldc" /* 18 */,
		"JBldcw" /* 19 */,
		"JBldc2lw" /* 20 */,
		"JBiload" /* 21 */,
		"JBlload" /* 22 */,
		"JBfload" /* 23 */,
		"JBdload" /* 24 */,
		"JBaload" /* 25 */,
		"JBiload0" /* 26 */,
		"JBiload1" /* 27 */,
		"JBiload2" /* 28 */,
		"JBiload3" /* 29 */,
		"JBlload0" /* 30 */,
		"JBlload1" /* 31 */,
		"JBlload2" /* 32 */,
		"JBlload3" /* 33 */,
		"JBfload0" /* 34 */,
		"JBfload1" /* 35 */,
		"JBfload2" /* 36 */,
		"JBfload3" /* 37 */,
		"JBdload0" /* 38 */,
		"JBdload1" /* 39 */,
		"JBdload2" /* 40 */,
		"JBdload3" /* 41 */,
		"JBaload0" /* 42 */,
		"JBaload1" /* 43 */,
		"JBaload2" /* 44 */,
		"JBaload3" /* 45 */,
		"JBiaload" /* 46 */,
		"JBlaload" /* 47 */,
		"JBfaload" /* 48 */,
		"JBdaload" /* 49 */,
		"JBaaload" /* 50 */,
		"JBbaload" /* 51 */,
		"JBcaload" /* 52 */,
		"JBsaload" /* 53 */,
		"JBistore" /* 54 */,
		"JBlstore" /* 55 */,
		"JBfstore" /* 56 */,
		"JBdstore" /* 57 */,
		"JBastore" /* 58 */,
		"JBistore0" /* 59 */,
		"JBistore1" /* 60 */,
		"JBistore2" /* 61 */,
		"JBistore3" /* 62 */,
		"JBlstore0" /* 63 */,
		"JBlstore1" /* 64 */,
		"JBlstore2" /* 65 */,
		"JBlstore3" /* 66 */,
		"JBfstore0" /* 67 */,
		"JBfstore1" /* 68 */,
		"JBfstore2" /* 69 */,
		"JBfstore3" /* 70 */,
		"JBdstore0" /* 71 */,
		"JBdstore1" /* 72 */,
		"JBdstore2" /* 73 */,
		"JBdstore3" /* 74 */,
		"JBastore0" /* 75 */,
		"JBastore1" /* 76 */,
		"JBastore2" /* 77 */,
		"JBastore3" /* 78 */,
		"JBiastore" /* 79 */,
		"JBlastore" /* 80 */,
		"JBfastore" /* 81 */,
		"JBdastore" /* 82 */,
		"JBaastore" /* 83 */,
		"JBbastore" /* 84 */,
		"JBcastore" /* 85 */,
		"JBsastore" /* 86 */,
		"JBpop" /* 87 */,
		"JBpop2" /* 88 */,
		"JBdup" /* 89 */,
		"JBdupx1" /* 90 */,
		"JBdupx2" /* 91 */,
		"JBdup2" /* 92 */,
		"JBdup2x1" /* 93 */,
		"JBdup2x2" /* 94 */,
		"JBswap" /* 95 */,
		"JBiadd" /* 96 */,
		"JBladd" /* 97 */,
		"JBfadd" /* 98 */,
		"JBdadd" /* 99 */,
		"JBisub" /* 100 */,
		"JBlsub" /* 101 */,
		"JBfsub" /* 102 */,
		"JBdsub" /* 103 */,
		"JBimul" /* 104 */,
		"JBlmul" /* 105 */,
		"JBfmul" /* 106 */,
		"JBdmul" /* 107 */,
		"JBidiv" /* 108 */,
		"JBldiv" /* 109 */,
		"JBfdiv" /* 110 */,
		"JBddiv" /* 111 */,
		"JBirem" /* 112 */,
		"JBlrem" /* 113 */,
		"JBfrem" /* 114 */,
		"JBdrem" /* 115 */,
		"JBineg" /* 116 */,
		"JBlneg" /* 117 */,
		"JBfneg" /* 118 */,
		"JBdneg" /* 119 */,
		"JBishl" /* 120 */,
		"JBlshl" /* 121 */,
		"JBishr" /* 122 */,
		"JBlshr" /* 123 */,
		"JBiushr" /* 124 */,
		"JBlushr" /* 125 */,
		"JBiand" /* 126 */,
		"JBland" /* 127 */,
		"JBior" /* 128 */,
		"JBlor" /* 129 */,
		"JBixor" /* 130 */,
		"JBlxor" /* 131 */,
		"JBiinc" /* 132 */,
		"JBi2l" /* 133 */,
		"JBi2f" /* 134 */,
		"JBi2d" /* 135 */,
		"JBl2i" /* 136 */,
		"JBl2f" /* 137 */,
		"JBl2d" /* 138 */,
		"JBf2i" /* 139 */,
		"JBf2l" /* 140 */,
		"JBf2d" /* 141 */,
		"JBd2i" /* 142 */,
		"JBd2l" /* 143 */,
		"JBd2f" /* 144 */,
		"JBi2b" /* 145 */,
		"JBi2c" /* 146 */,
		"JBi2s" /* 147 */,
		"JBlcmp" /* 148 */,
		"JBfcmpl" /* 149 */,
		"JBfcmpg" /* 150 */,
		"JBdcmpl" /* 151 */,
		"JBdcmpg" /* 152 */,
		"JBifeq" /* 153 */,
		"JBifne" /* 154 */,
		"JBiflt" /* 155 */,
		"JBifge" /* 156 */,
		"JBifgt" /* 157 */,
		"JBifle" /* 158 */,
		"JBificmpeq" /* 159 */,
		"JBificmpne" /* 160 */,
		"JBificmplt" /* 161 */,
		"JBificmpge" /* 162 */,
		"JBificmpgt" /* 163 */,
		"JBificmple" /* 164 */,
		"JBifacmpeq" /* 165 */,
		"JBifacmpne" /* 166 */,
		"JBgoto" /* 167 */,
		"JBunimplemented" /* 168 */,
		"JBunimplemented" /* 169 */,
		"JBtableswitch" /* 170 */,
		"JBlookupswitch" /* 171 */,
		"JBreturn0" /* 172 */,
		"JBreturn1" /* 173 */,
		"JBreturn2" /* 174 */,
		"JBsyncReturn0" /* 175 */,
		"JBsyncReturn1" /* 176 */,
		"JBsyncReturn2" /* 177 */,
		"JBgetstatic" /* 178 */,
		"JBputstatic" /* 179 */,
		"JBgetfield" /* 180 */,
		"JBputfield" /* 181 */,
		"JBinvokevirtual" /* 182 */,
		"JBinvokespecial" /* 183 */,
		"JBinvokestatic" /* 184 */,
		"JBinvokeinterface" /* 185 */,
		"JBunimplemented" /* 186 */,
		"JBnew" /* 187 */,
		"JBnewarray" /* 188 */,
		"JBanewarray" /* 189 */,
		"JBarraylength" /* 190 */,
		"JBathrow" /* 191 */,
		"JBcheckcast" /* 192 */,
		"JBinstanceof" /* 193 */,
		"JBmonitorenter" /* 194 */,
		"JBmonitorexit" /* 195 */,
		"JBunimplemented" /* 196 */,
		"JBmultianewarray" /* 197 */,
		"JBifnull" /* 198 */,
		"JBifnonnull" /* 199 */,
		"JBgotow" /* 200 */,
		"JBunimplemented" /* 201 */,
		"JBbreakpoint" /* 202 */,
		"JBiloadw" /* 203 */,
		"JBlloadw" /* 204 */,
		"JBfloadw" /* 205 */,
		"JBdloadw" /* 206 */,
		"JBaloadw" /* 207 */,
		"JBistorew" /* 208 */,
		"JBlstorew" /* 209 */,
		"JBfstorew" /* 210 */,
		"JBdstorew" /* 211 */,
		"JBastorew" /* 212 */,
		"JBiincw" /* 213 */,
		"JBunimplemented" /* 214 */,
		"JBaload0getfield" /* 215 */,
		"JBunimplemented" /* 216 */,
		"JBunimplemented" /* 217 */,
		"JBunimplemented" /* 218 */,
		"JBunimplemented" /* 219 */,
		"JBunimplemented" /* 220 */,
		"JBunimplemented" /* 221 */,
		"JBunimplemented" /* 222 */,
		"JBunimplemented" /* 223 */,
		"JBunimplemented" /* 224 */,
		"JBunimplemented" /* 225 */,
		"JBunimplemented" /* 226 */,
		"JBunimplemented" /* 227 */,
		"JBreturnFromConstructor" /* 228 */,
		"JBgenericReturn" /* 229 */,
		"JBunimplemented" /* 230 */,
		"JBinvokeinterface2" /* 231 */,
		"JBunimplemented" /* 232 */,
		"JBunimplemented" /* 233 */,
		"JBunimplemented" /* 234 */,
		"JBunimplemented" /* 235 */,
		"JBunimplemented" /* 236 */,
		"JBunimplemented" /* 237 */,
		"JBunimplemented" /* 238 */,
		"JBunimplemented" /* 239 */,
		"JBunimplemented" /* 240 */,
		"JBunimplemented" /* 241 */,
		"JBunimplemented" /* 242 */,
		"JBreturnToMicroJIT" /* 243 */,
		"JBretFromNative0" /* 244 */,
		"JBretFromNative1" /* 245 */,
		"JBretFromNativeF" /* 246 */,
		"JBretFromNativeD" /* 247 */,
		"JBretFromNativeJ" /* 248 */,
		"JBldc2dw" /* 249 */,
		"JBasyncCheck" /* 250 */,
		"JBunimplemented" /* 251 */,
		"JBunimplemented" /* 252 */,
		"JBunimplemented" /* 253 */,
		"JBimpdep1" /* 254 */,
		"JBimpdep2" /* 255 */
		};
	
	private byte [] byteCodeSize = 
	{
			   1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, //0-15 
			   2, 3, 2, 3, 3, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, //16-31
			   1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, //32-47
			   1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, //48-63
			   1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, //64-79
			   1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, //80-95
			   1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, //96-111
			   1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, //112-127
			   1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, //128-143
			   1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, //144-159
			   3, 3, 3, 3, 3, 3, 3, 3, 0, 2,-1,-1, 1, 1, 1, 1, //160-175
			   1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 1, 3, 2, 3, 1, 1, //176-191
			   3, 3, 1, 1, 0, 4, 3, 3, 5, 0, 1, 3, 3, 3, 3, 3, //192-207
			   3, 3, 3, 3, 3, 5, 3, 1, 1, 1, 1, 1, 3, 3, 3, 3, //208-223
			   3, 3, 3, 3, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, //224-239
			   0, 0, 0, 5, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1  //240-255
	};
	
	private byte [] byteCodeOperatorType = 
	{
			   0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //0-15
			   1, 1, 2, 2, 2, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, //16-31
			   0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //32-47
			   0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, //48-63
			   0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //64-79
			   0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //80-95
			   0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //96-111
			   0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //112-127
			   0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //128-143
			   0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 5, 5, 5, 5, 5, //144-159
			   5, 5, 5, 5, 5, 5, 5, 5,-1, 3,-1,-1, 0, 0, 0, 0, //160-175
			   0, 0, 2, 2, 2, 2, 2, 2, 2, 6,-1, 2, 7, 2, 0, 0, //176-191
			   2, 2, 0, 0,-1, 8, 5, 5, 9,-1,-1,-1,-1,-1,-1,-1, //192-207
			  -1,-1,-1,-1,-1,-1,-1, 0,-1,-1,-1,-1,-1,-1,-1,-1, //208-223
			  -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1, //224-239
			  -1,-1,-1,-1,-1,-1,-1,-1,-1, 2,-1,-1,-1,-1,-1,-1  //240-255
	};
	
	public  static String getBytecode ( int bc )
	{
		return byteCode[bc];
	}
	
	public int getByteCodeSize ( int byteCode )
	{
		return (int)byteCodeSize[byteCode];
	}
	
	public int getByteCodeOperatorType ( int byteCode )
	{
		return (int)byteCodeOperatorType[byteCode];
	}
	
	public boolean isFloatByteCode ( int byteCode ) //this is for a trick
	{
		return Pattern.matches("JBf.*", getBytecode (byteCode));
	}
	
	public boolean isInvokeBytecode ( int byteCode )
	{
		return Pattern.matches("JBinvoke.*", getBytecode(byteCode));
	}
		
	
}
