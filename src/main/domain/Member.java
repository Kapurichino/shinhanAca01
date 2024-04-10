package main.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {
	private String id;
	private String pwd;
	private String name;
	private String tel;
	private String member_type;

	public void updateMember(String pwd, String name, String tel, String member_type){
		this.pwd = pwd;
		this.name = name;
		this.tel = tel;
		this.member_type = member_type;
	}
}
