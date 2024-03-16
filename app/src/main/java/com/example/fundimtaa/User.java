package com.example.fundimtaa;
public class User {
        private String userId;
        private String name;
        private String email;
        private String mobile;

        public User() {
            // Default constructor required for Firebase
        }

        public User(String userId, String name, String email, String mobile) {
            this.userId = userId;
            this.name = name;
            this.email = email;
            this.mobile = mobile;
        }

        // Getters and setters for the fields
        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
    }


