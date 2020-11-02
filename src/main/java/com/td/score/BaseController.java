package com.td.score;

import com.td.score.entity.ScoreDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class BaseController {

	@Autowired
	ScoreDao scoreDao;

	static Integer x_interval;
	static Integer y_interval;
	static Integer x_start;
	static Integer y_start;
	static Integer ms_per;
	static Integer sep;

	static void removeEmpty(String s){
		s = s.replaceAll(" ", "");
		s = s.replaceAll("\r|\n", "");
	}

	public static String convertByType(String y, String yg, String yc) throws Exception {
		removeEmpty(y);
		removeEmpty(yg);
		removeEmpty(yc);

		int length = y.length();
		if (yg.length()!=length||yc.length()!=length) throw new Exception("长度不一致");

		StringBuffer source = new StringBuffer();
		for (int i = 0; i < length; i++) {
			source.append(y.substring(i, i+1)+yg.substring(i, i+1)+yc.substring(i, i+1));
			source.append(",");
		}

		return convert(source.toString());
	}

	public static String convert(String source){
		StringBuffer converted = new StringBuffer();

		removeEmpty(source);

		String[] notes = source.split(",");
		for (int i = 0; i<notes.length; i++){
			String note = notes[i];

			if (note.equals("")) continue;
			if (note.equals("---")){
				converted.append("Delay "+  ms_per);
				converted.append("\n");
				continue;
			}

			Integer y = Integer.valueOf(note.substring(0,1));
			Integer yg = Integer.valueOf(note.substring(1,2));
			Integer yc = Integer.valueOf(note.substring(2,3));

			int p_x = x_start+(y-1)*x_interval;
			int p_y = y_start-(yg-1)*y_interval;
			converted.append("MoveTo "+p_x+", "+p_y);
			converted.append("\n");

			converted.append("LeftClick 1");
			converted.append("\n");
			double yc_ms = ((new BigDecimal(yc).divide(new BigDecimal(sep)).doubleValue())) * ms_per;
			converted.append("Delay "+ (int)yc_ms);
			converted.append("\n");
		}
		return converted.toString();
	}

	@PostMapping("convert")
	Result convert(@RequestBody Map<String, String> arg) throws Exception {
		x_interval = Integer.valueOf(arg.get("x_interval"));
		y_interval = Integer.valueOf(arg.get("y_interval"));
		x_start = Integer.valueOf(arg.get("x_start"));
		y_start = Integer.valueOf(arg.get("y_start"));
		ms_per = Integer.valueOf(arg.get("ms_per"));
		sep = Integer.valueOf(arg.get("sep"));

		String sourceType = arg.get("sourceType");
		if (sourceType.equals("1")){
			String y = arg.get("y");
			String yg = arg.get("yg");
			String yc = arg.get("yc");
			return Result.success(convertByType(y, yg, yc));
		}else {
			String source = arg.get("source");
			return Result.success(convert(source));
		}

	}

}
