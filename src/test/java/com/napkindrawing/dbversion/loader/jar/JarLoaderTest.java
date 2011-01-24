package com.napkindrawing.dbversion.loader.jar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Property;
import org.junit.Test;

import com.napkindrawing.dbversion.task.DbVersionInfo;
import com.napkindrawing.dbversion.task.DbVersionInit;
import com.napkindrawing.dbversion.task.DbVersionUpgrade;


public class JarLoaderTest {
    
    @Test
    public void testProfileStuff() {
        DbVersionInfo infoTask = new DbVersionInfo();
        
        infoTask.setCheckInitTables(false);
        infoTask.setLoaderSpecClass(JarLoader.class);
        
        Property prop = new Property();
        prop.setName("jar.path");
        prop.setValue("com/napkindrawing/test-sql.jar");
        
        infoTask.addConfiguredLoaderProperty(prop);

        infoTask.init();
        infoTask.execute();

        System.out.println("Profile Count: " + infoTask.getProfiles().size());
    }
	
	@Test
	public void testDbVersionInfo() throws SQLException {
	    
	    Connection conn = DriverManager.getConnection(
            "jdbc:mysql://localhost",
            "root",
            "toor"
	    ); 
	    
	    Statement stmt = conn.createStatement();
	    System.out.println("Dropping dbversiontest");
	    stmt.executeUpdate("DROP DATABASE IF EXISTS dbversiontest");

        System.out.println("Creating dbversiontest");
	    stmt.executeUpdate("CREATE DATABASE dbversiontest");
	    
		DbVersionInit dbviTask = new DbVersionInit();
		
		dbviTask.setLoaderSpecClass(JarLoader.class);
		dbviTask.setUserid("root");
		dbviTask.setPassword("toor");
		dbviTask.setUrl("jdbc:mysql://localhost/dbversiontest");
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
		
		DbVersionUpgrade dbvuTask = new DbVersionUpgrade();

        dbvuTask.setLoaderSpecClass(JarLoader.class);
        dbvuTask.setUserid("root");
        dbvuTask.setPassword("toor");
        dbvuTask.setUrl("jdbc:mysql://localhost/dbversiontest");
        dbvuTask.setDriver("com.mysql.jdbc.Driver");
        
        dbvuTask.setProfileNames("bar,foo");
        
        dbvuTask.setProject(new Project());
        
        Property propu = new Property();
        propu.setName("jar.path");
        propu.setValue("com/napkindrawing/test-sql.jar");
       
        dbvuTask.addConfiguredLoaderProperty(prop);
        
        dbvuTask.init();
        dbvuTask.execute();

	}
}
