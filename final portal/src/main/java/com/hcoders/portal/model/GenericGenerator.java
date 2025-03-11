package com.hcoders.portal.model;

import org.hibernate.annotations.Parameter;

public @interface GenericGenerator {

	String name();

	String strategy();

	Parameter[] parameters();

}
