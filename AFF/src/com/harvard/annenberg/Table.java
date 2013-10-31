package com.harvard.annenberg;

import java.util.ArrayList;

/*
 * This is the data for a table.
 */
public class Table {
	int number;
	int numFriends;
	int numPeople;
	ArrayList<Person> friends;
	ArrayList<Person> others;
	ArrayList<Person> all;
	static int MAX_PEOPLE = 20;

	public Table(int tableNum) {
		number = tableNum;
		numFriends = 0;
		friends = new ArrayList<Person>();
		others = new ArrayList<Person>();
		all = new ArrayList<Person>();
		numPeople = 0;
	}

	public int getNumber() {
		return number;
	}

	public int getNumPeople() {
		return numPeople;
	}

	public int getNumFriends() {
		return numFriends;
	}

	public ArrayList<Person> getFriends() {
		return friends;
	}

	public ArrayList<Person> getOthers() {
		return others;
	}

	public boolean addFriend(Person friend) {
		friends.add(friend);
		all.add(numFriends, friend);
		numFriends++;
		numPeople++;
		return true;
	}

	public boolean removeFriend(Person friend) {
		if (friends.remove(friend)) {
			all.remove(friend);
			numFriends--;
			numPeople--;
			return true;
		}

		return false;
	}

	public boolean addOther(Person other) {
		others.add(other);
		all.add(other);
		numPeople++;
		return true;
	}

	public boolean removeOther(Person other) {
		if (others.remove(other)) {
			all.remove(other);
			numPeople--;
			return true;
		}
		return false;
	}

	public ArrayList<Person> getAll() {
		return all;
	}
}
