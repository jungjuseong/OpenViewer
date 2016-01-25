package com.mypackage;

public class Main {

	public static void main(String args) {

		if (args.length() > 1)
			System.out.println("Hello " + args[1]);
		else
			System.out.println("Hello my package main");
	}
}
