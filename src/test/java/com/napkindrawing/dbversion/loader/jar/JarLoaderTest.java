package com.napkindrawing.dbversion.loader.jar;

import org.apache.tools.ant.taskdefs.Property;
import org.junit.Test;

import com.napkindrawing.dbversion.task.DbVersionInit;


public class JarLoaderTest {
	
	@Test
	public void testDbVersionInfo() {
		DbVersionInit dbviTask = new DbVersionInit();
		dbviTask.setLoaderSpecClass(JarLoader.class);
		dbviTask.setUserid("root");
		dbviTask.setPassword("toor");
		dbviTask.setUrl("jdbc:mysql://localhost/foo");
		dbviTask.setDriver("com.mysql.jdbc.Driver");
		dbviTask.setForceInit(true);
		Property prop = new Property();
		prop.setName("jar.path");
		prop.setValue("com/napkindrawing/test-sql.jar");
		dbviTask.addConfiguredLoaderProperty(prop);
		System.out.println("created DbVersionInit task, calling init");
		dbviTask.init();
		System.out.println("initialized DbVersionInit task, executing");
		dbviTask.execute();
		System.out.println("Number of profiles: " + dbviTask.getProfiles().size());
	}
}
