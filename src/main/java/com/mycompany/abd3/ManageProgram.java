/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.abd3;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author skurc
 */
public class ManageProgram {

    Scanner reader;
    Session session;
    String username;
    public ManageProgram(){
        session = HibernateUtil.getSessionFactory().openSession();
        reader = new Scanner(System.in); 
        System.out.println("    Insert username");
        username = reader.next();

        while (true) {
            int option = mainMenu();
            if (option == 1) {
                createIssue();
            } 
            else if (option == 2) {
                showIssues();
            } 
            else if(option ==3){
                
                System.out.println("    Insert issue id");
                BigDecimal issueID = new BigDecimal(reader.nextInt());
                ManageIssue manageIssue = new ManageIssue(issueID, username, session);
            }
            else if(option == 4){
                System.out.println("    Number of issues created by you is: " + showNumberOfMyIssues());
            }
            else if(option == 5){
                 break;
            }
        }
    }
    
    public int mainMenu() {
        System.out.println("Select:");
        System.out.println("    1 Add Issue");
        System.out.println("    2 Show Issues");
        System.out.println("    3 Manage Issue");
        System.out.println("    4 Show number of my issues");
        System.out.println("    5 Exit");
        System.out.println("[1-5]");
        
        int n = reader.nextInt();
        
        return n;
    }
    
    
    public void showIssues() {

        Query issues = session.createQuery("from Issue where author=:username");
        issues.setParameter("username", username);
        List<Issue> issues2 = issues.list();
        
        
        System.out.println("IssueId\t\tAuthor\t\tDate\t\tCoordinator\t\tContent");
        for (Issue i : issues2) {
            System.out.println(i.getIssueId() + "\t\t" + i.getAuthor() + "\t\t" + i.getDate() + "\t\t" + i.getCoordinator() + "\t\t" + i.getContent());
        }
    }
    
    public void createIssue(){
        Query id = session.createQuery("select max(issueId) from Issue");
        List ids = id.list();

        BigDecimal n = (BigDecimal)ids.get(0);
        
        BigDecimal newId = n.add(new BigDecimal(1));
        Date date = new Date();
        
        System.out.println("    Insert coordinator");
        String coordinator = reader.next();
        
        System.out.println("    Insert content");
        String content = reader.next();
        
        Issue u = null;
        session.beginTransaction();
            u = new Issue();
            u.setIssueId(newId);
            u.setAuthor(username);
            u.setDate(date);
            u.setCoordinator(coordinator);
            u.setContent(content);
            session.save(u);
            session.getTransaction().commit();
    }
    
    public String showNumberOfMyIssues(){
        Query numberOfMyIssues = session.createQuery("select count(issueId) from Issue where author=:username");
        List l = numberOfMyIssues.setParameter("username", username).list();
        return l.get(0).toString();
    }
}
