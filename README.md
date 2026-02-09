# SkillSync LMS Core

A comprehensive backend for a Learning Management System (LMS) that simulates a real-world educational platform. This project focuses on Role-Based Access Control (RBAC), approval workflows, and data persistence using JSON.

## Technical Overview
### System Architecture
* **Tech Stack:** Java Backend with Swing components for the Dashboard UI.
* **JSON Persistence:** The system utilizes `courses.json` and `users.json` to store application state. This ensures data portability and persistence across application restarts.
* **OOP Principles:** Heavy use of Inheritance and Polymorphism to handle different user types (Students vs. Instructors) and course states.

### Functional Specifications
#### 1. Admin & Instructor Workflows
* **Course Submission:** Instructors can create detailed course modules and submit them to the system.
* **Approval Pipeline:** Submitted courses enter a `PENDING` state. Administrators review these courses and can set them to `APPROVED` or `REJECTED`.
* **Visibility Logic:** Only courses explicitly `APPROVED` by an admin become visible in the student catalog.

#### 2. Student Features & Certification
* **Dashboard:** Students can browse the catalog of approved courses and enroll.
* **Assessment:** The system supports quiz mechanisms to test student knowledge.
* **Certification:** Upon successful completion of a course, the system generates a "Certificate Earned" record, which students can view or download from their dashboard.

#### 3. Analytics
* **Instructor Insights:** The system calculates and displays performance metrics, helping instructors understand how students are performing in their courses.

## Contributors
* Mohamed Bahig
* Sara Hany
* Hayat Tarek
* Shaden Rafik
