package com.td.score;

import lombok.Data;

@Data
public class Result {

	String msg;
	boolean status;
	int code;
	Object data;

	public static Result success(){
		return new Result(){{
			setCode(0);
			setMsg("");
			setStatus(true);
		}};
	}

	public static Result success(Object data1){
		return new Result(){{
			setCode(0);
			setMsg("");
			setStatus(true);
			setData(data1);
		}};
	}

	public static Result error(String msg){
		return new Result(){{
			setCode(-1);
			setMsg(msg);
			setStatus(false);
		}};
	}
}
