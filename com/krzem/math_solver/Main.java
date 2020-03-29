package com.krzem.math_solver;



import java.lang.Math;



public class Main{
	public static void main(String[] args){
		new Main((args.length==0?new String[]{"((8^4+3)-10^5)*567-7883784^2"}:args));
	}



	private static final String VALID_SYMBOLS="1234567890+-*/^()";



	public Main(String[] a){
		for (int i=0;i<a.length;i++){
			System.out.printf("%sEQUASION #%d:\n",(i==0?"":"\n"),i+1);
			String err=this._valid(a[i]);
			if (err.length()>0){
				System.out.println(err);
				continue;
			}
			String eq=a[i];
			System.out.printf("  PROBLEM:\n    %s = ?\n\n",eq);
			int j=1;
			while (true){
				String[] stp=this._step(eq,j);
				if (stp==null){
					break;
				}
				eq=stp[0];
				System.out.println(stp[1]);
				j++;
			}
			System.out.printf("  RESULT:\n    %s = %s\n",a[i],eq);
		}
	}



	private String _valid(String eq){
		if (eq.length()==0){
			String pad="";
			for (int j=0;j<eq.length();j++){
				pad+=" ";
			}
			return "  ERROR:\n    Invalid Symbol: Empty Equasion\n\n      ^";
		}
		int b=0;
		for (int i=0;i<eq.length();i++){
			if (VALID_SYMBOLS.indexOf(String.valueOf(eq.charAt(i)))==-1){
				return String.format("  ERROR:\n    Invalid Symbol: Invalid Symbol\n      %s\n      %s^",eq,this._pad(i," "));
			}
			else if (eq.charAt(i)=='('){
				b++;
			}
			else if (eq.charAt(i)==')'){
				b--;
				if (b<0){
					return String.format("  ERROR:\n    Invalid Symbol: Extra Bracket\n      %s\n      %s^",eq,this._pad(i," "));
				}
			}
		}
		if (b>0){
			return String.format("  ERROR:\n    Invalid Symbol: Unclosed Bracket\n      %s\n      %s^",eq,this._pad(eq.length()," "));
		}
		return "";
	}



	private String[] _step(String eq,int i){
		if (eq.indexOf("(")>-1){
			Object[] _br=this._max_balance_brackets(eq);
			int si=(Integer)_br[0];
			String br=(String)_br[1];
			String[] _stp=this._calc_step(br);
			if (!this._no_operation(_stp[0])){
				_stp[0]="("+_stp[0]+")";
			}
			return new String[]{eq.substring(0,si)+_stp[0]+eq.substring(si+br.length()+2),String.format("  STEP #%d:\n    Evaluate Brackets: %s:\n      %s\n      %s%s\n",i,_stp[1],eq.substring(0,si)+_stp[0]+eq.substring(si+br.length()+2),this._pad(si+(_stp[0].charAt(0)=='('?1:0)+Integer.parseInt(_stp[2])," "),this._pad(Integer.parseInt(_stp[3]),"^"))};
		}
		else{
			if (this._no_operation(eq)){
				return null;
			}
			String[] _stp=this._calc_step(eq);
			return new String[]{_stp[0],String.format("  STEP #%d:\n    %s:\n      %s\n      %s%s\n",i,_stp[1],_stp[0],this._pad(Integer.parseInt(_stp[2])," "),this._pad(Integer.parseInt(_stp[3]),"^"))};
		}
	}



	private String[] _calc_step(String eq){
		if (eq.indexOf("^")>-1){
			String[] _pow=this._extract_math_sign(eq,'^');
			eq=_pow[0]+this._calc_pow(_pow[1],_pow[2])+_pow[3];
			return new String[]{eq,"Evaluate Power",Integer.toString(_pow[0].length()),Integer.toString(this._calc_pow(_pow[1],_pow[2]).length())};
		}
		else if (eq.indexOf("*")>-1&&((eq.indexOf("/")>-1&&eq.indexOf("*")<eq.indexOf("/"))||eq.indexOf("/")==-1)){
			String[] _mult=this._extract_math_sign(eq,'*');
			eq=_mult[0]+this._calc_mult(_mult[1],_mult[2])+_mult[3];
			return new String[]{eq,"Evaluate Multiplication",Integer.toString(_mult[0].length()),Integer.toString(this._calc_mult(_mult[1],_mult[2]).length())};
		}
		else if (eq.indexOf("/")>-1&&((eq.indexOf("*")>-1&&eq.indexOf("/")<eq.indexOf("*"))||eq.indexOf("*")==-1)){
			String[] _div=this._extract_math_sign(eq,'/');
			eq=_div[0]+this._calc_div(_div[1],_div[2])+_div[3];
			return new String[]{eq,"Evaluate Division",Integer.toString(_div[0].length()),Integer.toString(this._calc_div(_div[1],_div[2]).length())};
		}
		else if (eq.indexOf("+")>-1&&((eq.indexOf("-")>-1&&eq.indexOf("+")<eq.indexOf("-"))||eq.indexOf("-")==-1)){
			String[] _add=this._extract_math_sign(eq,'+');
			eq=_add[0]+this._calc_add(_add[1],_add[2])+_add[3];
			return new String[]{eq,"Evaluate Addition",Integer.toString(_add[0].length()),Integer.toString(this._calc_add(_add[1],_add[2]).length())};
		}
		else if (eq.indexOf("-")>-1&&((eq.indexOf("+")>-1&&eq.indexOf("-")<eq.indexOf("+"))||eq.indexOf("+")==-1)){
			String[] _sub=this._extract_math_sign(eq,'-');
			eq=_sub[0]+this._calc_sub(_sub[1],_sub[2])+_sub[3];
			return new String[]{eq,"Evaluate Subtraction",Integer.toString(_sub[0].length()),Integer.toString(this._calc_sub(_sub[1],_sub[2]).length())};
		}
		return new String[]{eq,"No Operation","0","0"};
	}



	private String _calc_pow(String a,String b){
		return Long.toString((int)Math.pow(Long.parseLong(a),Long.parseLong(b)));
	}



	private String _calc_mult(String a,String b){
		return Long.toString(Long.parseLong(a)*Long.parseLong(b));
	}



	private String _calc_div(String a,String b){
		return Long.toString(Long.parseLong(a)/Long.parseLong(b));
	}


	private String _calc_add(String a,String b){
		return Long.toString(Long.parseLong(a)+Long.parseLong(b));
	}



	private String _calc_sub(String a,String b){
		return Long.toString(Long.parseLong(a)-Long.parseLong(b));
	}



	private Object[] _max_balance_brackets(String eq){
		int b=0;
		int mb=this._max_balance(eq);
		for (int i=0;i<eq.length();i++){
			if (eq.charAt(i)=='('){
				b++;
				if (b==mb){
					return new Object[]{i,eq.substring(i+1,i+eq.substring(i).indexOf(")"))};
				}
			}
			else if (eq.charAt(i)==')'){
				b--;
			}
		}
		return null;
	}



	private int _max_balance(String eq){
		int b=0;
		int mb=-1;
		for (int i=0;i<eq.length();i++){
			if (eq.charAt(i)=='('){
				b++;
				mb=Math.max(mb,b);
			}
			else if (eq.charAt(i)==')'){
				b--;
			}
		}
		return mb;
	}



	private String[] _extract_math_sign(String eq,char s){
		String a="";
		String n1="";
		String n2="";
		String b="";
		boolean st=false;
		for (int i=0;i<eq.length();i++){
			if (i>0&&eq.charAt(i)==s){
				st=true;
			}
			else if ("0123456789".indexOf(String.valueOf(eq.charAt(i)))>-1||((st==false&&n1.length()==0&&eq.charAt(i)=='-')||(st==true&&n2.length()==0&&eq.charAt(i)=='-'))){
				if (st==false){
					n1+=String.valueOf(eq.charAt(i));
				}
				else{
					n2+=String.valueOf(eq.charAt(i));
				}
			}
			else{
				if (st==true){
					b=eq.substring(i);
					break;
				}
				else{
					a+=n1+String.valueOf(eq.charAt(i));
					n1="";
				}
			}
		}
		return new String[]{a,n1,n2,b};
	}



	private boolean _no_operation(String eq){
		for (int i=(eq.charAt(0)=='-'?1:0);i<eq.length();i++){
			if ("0123456789".indexOf(String.valueOf(eq.charAt(i)))==-1){
				return false;
			}
		}
		return true;
	}



	private String _pad(int n,String s){
		String p="";
		for (int i=0;i<n;i++){
			p+=s;
		}
		return p;
	}
}