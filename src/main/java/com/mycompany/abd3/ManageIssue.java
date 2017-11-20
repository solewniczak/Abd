/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.abd3;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author skurc
 */
public class ManageIssue {

    Session session;
    Issue issue;
    Scanner reader;
    String author;

    public ManageIssue(BigDecimal IssueID, String author, Session s) {
        reader = new Scanner(System.in);
        this.author = author;
        session = s;
        this.issue = (Issue) session.get(Issue.class, IssueID);
        if (this.issue == null){
            System.out.println("Issue doesn't exist!");
            return; 
        }
        int n = -1;
        while (true) {
            n = menu();
            if (n == 1) {
                addComment();
            } else if (n == 2) {
                addCause();
            } else if (n == 3) {
                addTask();
            } else if (n == 4) {
                closeIssue();
            } else if (n == 5) {
                showEvents();
            } else if (n == 6) {
                updateIssue();
            } else if (n == 7) {
                deleteIssue();
                break;
            } else if (n == 8) {
                break;
            }
        }
    }

    public int menu() {
        System.out.println("Select:");
        System.out.println("    1 Add Comment");
        System.out.println("    2 Add Cause");
        System.out.println("    3 Add Task");
        System.out.println("    4 Close Issue");
        System.out.println("    5 Show events");
        System.out.println("    6 Edit content");
        System.out.println("    7 Delete issue");
        System.out.println("    8 Exit");
        System.out.println("[1-7]");

        Scanner reader = new Scanner(System.in);
        int n = reader.nextInt();
        return n;

    }

    public BigDecimal getNewID(String query) {
        Query id = session.createQuery(query);
        List ids = id.list();
        Scanner reader = new Scanner(System.in);
        BigDecimal n = (BigDecimal) ids.get(0);

        if (n == null) {
            return new BigDecimal(1);
        } else {
            BigDecimal newId = n.add(new BigDecimal(1));
            return newId;
        }
    }

    public void addComment() {
        BigDecimal n = this.getNewID("select max(commentId) from Comment");

        Date date = new Date();

        System.out.println("    Insert content");
        String content = reader.next();

        Comment u = null;
        session.beginTransaction();
        u = new Comment();
        u.setCommentId(n);
        u.setIssue(this.issue);
        u.setAuthor(author);
        u.setDate(date);
        u.setContent(content);
        session.save(u);
        session.getTransaction().commit();
    }

    public void addCause() {
        BigDecimal n = this.getNewID("select max(causeId) from Cause");

        Date date = new Date();
        System.out.println("    Insert content");
        String content = reader.next();

        Cause u = null;
        session.beginTransaction();
        u = new Cause();
        u.setCauseId(n);
        u.setIssue(this.issue);
        u.setAuthor(author);
        u.setDate(date);
        u.setContent(content);
        session.save(u);
        session.getTransaction().commit();
    }

    public Date convertStringIntoDate(String dateStr) {
        DateFormat formatter = new SimpleDateFormat("MM/dd/yy");
        try {
            Date date = formatter.parse(dateStr);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addTask() {
        BigDecimal n = this.getNewID("select max(taskId) from Task");

        System.out.println("    Insert doer");
        String doer = reader.next();

        System.out.println("    Insert plain (MM/dd/yy)");
        String planStr = reader.next();
        Date plain = this.convertStringIntoDate(planStr);

        Date date = new Date();

        System.out.println("    Insert content");
        String content = reader.next();

        Task u = null;
        session.beginTransaction();
        u = new Task();
        u.setTaskId(n);
        u.setIssue(this.issue);
        u.setPlan(plain);
        u.setAuthor(author);
        u.setDoer(doer);
        u.setDate(date);
        u.setContent(content);
        session.save(u);
        session.getTransaction().commit();
    }

    public void closeIssue() {
        System.out.println("    Please create closing comment");
        BigDecimal n = this.getNewID("select max(closingCommentId) from ClosingComment");

        Date date = new Date();

        System.out.println("    Insert content");
        String content = reader.next();

        ClosingComment u = null;
        session.beginTransaction();
        u = new ClosingComment();
        u.setClosingCommentId(n);
        u.setIssue(this.issue);
        u.setAuthor(author);
        u.setDate(date);
        u.setContent(content);
        session.save(u);
        session.getTransaction().commit();
        System.out.println("    Issue closed");
    }

    public void showEvents() {
        System.out.println("    Comments:");
        Query comments = session.createQuery("from Comment where issue=:issue");
        comments.setParameter("issue", issue);
        showComment(comments);

        System.out.println("    Tasks:");
        Query tasks = session.createQuery("from Task where issue=:issue");
        tasks.setParameter("issue", issue);
        showTasks(tasks);

        System.out.println("    Causes:");
        Query causes = session.createQuery("from Cause where issue=:issue");
        causes.setParameter("issue", issue);
        showCause(causes);
    }

    public void showComment(Query q) {
        List<Comment> rows = q.list();

        System.out.println("CommentId\t\t\tAuthor\t\t\tDate\t\t\tContent\t\t\tIssueID");
        for (Comment i : rows) {
            String date = new SimpleDateFormat("yyyy-MM-dd").format(i.getDate());
            System.out.println(i.getCommentId() + "\t\t\t" + i.getAuthor() + "\t\t\t" + date + "\t\t\t"
                    + i.getContent() + "\t\t\t" + i.getIssue().getIssueId());
        }
    }

    public void showTasks(Query q) {
        List<Task> rows = q.list();

        System.out.println("TaskId\t\t\tAuthor\t\t\tDate\t\t\tContent\t\t\tdoer\t\t\tplan\t\t\tIssueID");
        for (Task i : rows) {
            String date = new SimpleDateFormat("yyyy-MM-dd").format(i.getDate());
            String plan = new SimpleDateFormat("yyyy-MM-dd").format(i.getPlan());
            System.out.println(i.getTaskId() + "\t\t\t" + i.getAuthor() + "\t\t\t" + date + "\t\t\t" + i.getContent() + "\t\t\t"
                    + i.getDoer() + "\t\t\t" + plan + "\t\t\t" + i.getIssue().getIssueId());
        }
    }

    public void showCause(Query q) {
        List<Cause> rows = q.list();

        System.out.println("CommentId\t\t\tAuthor\t\t\tDate\t\t\tContent\t\t\tIssueID");
        for (Cause i : rows) {
            String date = new SimpleDateFormat("yyyy-MM-dd").format(i.getDate());
            System.out.println(i.getCauseId() + "\t\t\t" + i.getAuthor() + "\t\t\t"
                    + date + "\t\t\t" + i.getContent() + "\t\t\t" + i.getIssue().getIssueId());
        }
    }

    public void updateIssue() {
        Query query = session.createQuery("update Issue set content = :content"
                + " where issueId = :issueId");

        System.out.println("    Insert new content");
        String content = reader.next();

        query.setParameter("content", content);
        query.setParameter("issueId", issue.getIssueId());
        int result = query.executeUpdate();
        System.out.println("Record updated");
    }

    public void deleteIssue() {
        Transaction transaction = session.beginTransaction();
        try {
            // your code
            String hql = "delete from Comment where issue=:issue";

            Query query = session.createQuery(hql);
            query.setParameter("issue", issue);

            System.out.println(query.executeUpdate());

            // your code
            hql = "delete from Cause where issue=:issue";

            query = session.createQuery(hql);
            query.setParameter("issue", issue);

            System.out.println(query.executeUpdate());

            // your code
            hql = "delete from Task where issue=:issue";

            query = session.createQuery(hql);
            query.setParameter("issue", issue);

            System.out.println(query.executeUpdate());
            
             hql = "delete from Issue where issueId=:issue";

            query = session.createQuery(hql);
            query.setParameter("issue", issue.getIssueId());

            System.out.println(query.executeUpdate());

            transaction.commit();

        } catch (Throwable t) {
            transaction.rollback();
            throw t;
        }
    }
}
