# cybersecuritybase-project

This is project, that should demonstrace at least 5 severe security issues from OWASP Top 10. 
This project was developed for the online course [CyberSecurity Base](https://cybersecuritybase.mooc.fi/) at the University of Helsinki.

## Project description
project goal is to allow users to upload and then download files to and from the server. 
Every user first creates an account, and then he can upload and download files from the server. 
The user can also add comments to the individual files. Because the files aren't shared, the comments are supposed to be information only for the user himself.

- Source codes: https://github.com/PatrikValkovic/cybersecuritybase-project.
- How to run the application: open project in your favorite IDE and run the application as standard Java application. However, compared to the starter code, the app requires additional dependencies that need to be downloaded.
- Technology: Java Spring Boot


## Issue: A1:2017 SQL Injection
Steps to reproduce:
1. Open the application. It should redirect you to the /login page. If not, go to that page directly.
2. Click Register.
3. Register new user. The application should redirect you to the /login page.
4. To the name field type "<registered name>' OR 'h'='h".
5. Type incorrect password or leave the password field empty.
6. Click Login. The application will log in the user.
The cause of the issue is that the application doesn't escape parameters of SQL queries. We can see this in the class sec.project.repository.UsersRepository in the find method. This method should find the user with specific email and password; however, because of the SQL injection, the query returns the user even if the password is incorrect.
We can prevent this by properly escaping the input from the user. The Spring framework can do this automatically, and I needed to connect directly to the database to allow this flow.


## Issue: A2:2017 Broken Authentication - improper session handling
Steps to reproduce:
1. Open the application. It should redirect you to the /login page. If not, go to that page directly.
2. Click Register.
3. Register new user. The application should redirect you to the /login page.
4. Log in as a newly created user.
5. Open browser's developer tools.
6. Examine the cookies. It is evident that the session's cookie is the user's id.
7. Change the session's cookie to the ID of the different user.
8. Explore files of the different user.

The problem with this kind of sessions handling is that the output is unencrypted and predictable. This way the attacker can look at another user files even without knowing the username. Moreover, we can assume that the IDs are sequential; thus it's easy for the attacker to list the users.

We can prevent this attack by using proper session handling (for example the built-in one in the Spring framework).


## Issue: A3:2017 Sensitive Data Exposure
Steps to reproduce: none

For this issue, we can use knowledge from the previous point. There is no doubt, that with the broken session handling when the attacker has the same rights as the user, the sensitive data are exposed. However, at this point I want to talk about something else, that is not obvious from the application. If we look at the sec.project.repository.UsersRepository.insert method, we can see that the password is in the database in plain-text. Although this flaw isn't evident, it represents a severe risk in case the database is leaked.

We can reduce this risk by encrypting the password with the strong hash function and by using password salting.


## Issue: A5:2017 Broken Access Control
Steps to reproduce:
1. Open the application. It should redirect you to the /login page. If not, go to that page directly.
2. Click Register.
3. Register new user. The application should redirect you to the /login page.
4. Log in as a newly created user.
5. Upload some file.
6. Click on the name of the file. The application will redirect you to the page with more information which URL is "/file/<hash>".
7. Log out by deleting the session's cookie.
8. Try to download the file as an unregistered user. You can see that the application allows you to access the file.
This issue is caused by the improper demands on the access rights. We can look into the class sec.project.controller.FileController, we can see that the user is not authorized at all. The app only relies on the fact, that the hashes of uploaded files are hard to find.
We can fix this issue by authenticating the user before the request with following authorization of the user's rights to access the file.


## Issue: A7:2017 Cross-Site Scripting (XSS)
Steps to reproduce:
1. Open the application. It should redirect you to the /login page. If not, go to that page directly.
2. Click Register.
3. Register new user. The application should redirect you to the /login page.
4. Log in as a newly created user.
5. Upload some file.
6. Click on the name of the file. The application will redirect you to the page with more information and with textarea field to add a comment.
7. Type `<script type="text/javascript">alert("You have been hacked")</script>` text to the comment and send it.
8. From now every time somebody opens the page, the alert box will be shown to him.

The code above can be changed for practically everything what the attacker will want to do.

The issue is caused because the application is not escaping the text from the untrusted sources (from the users) before it is shown on the page.

The fix of the situation is so easy as using the built-in capabilities of the template engine. I needed to use th:utext (unsecured text) attribute to allow this attack. Standard th:text attribute escapes the content properly.

----------------------

In the end, I want to point out, that because of the Spring framework, that tries to solve most of the security problems, it was much harder for me to build the application with security flaws than at least somehow secured application. I needed to handle the database connection and creating of the response by hand because of the technologies behind cares about the common security mistakes. I think that it's clear that frameworks and libraries like Spring, Hibernate and other helping us to build better and more secure applications.
