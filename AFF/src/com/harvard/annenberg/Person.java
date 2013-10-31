package com.harvard.annenberg;

/*
 * This is a person!
 */
public class Person {
	String name;
	String HUID;
	String img;
	String status;
	String table;
	String time;

	public Person(String HUID, String name, String img, String status,
			String table, String time) {
		super();
		this.HUID = HUID;
		this.name = name;
		this.img = img;
		this.status = status;
		this.table = table;
		this.time = time;
	}

	public String getHUID() {
		return HUID;
	}

	public String getName() {
		return name;
	}

	public String getImg() {
		return img;
	}

	public String getStatus() {
		return status;
	}

	public String getTable() {
		return table;
	}

	public String getTime() {
		return time;
	}

	public boolean equals(Object o) {
		Person person = (Person) o;
		return this.getHUID().equals(person.getHUID());
	}

}
