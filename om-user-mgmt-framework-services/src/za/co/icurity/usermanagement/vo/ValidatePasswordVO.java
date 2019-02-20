package za.co.icurity.usermanagement.vo;

public class ValidatePasswordVO {
    private String password;
      private String username;
      private String firstName;
      private String lastName;
      
      public ValidatePasswordVO() {
          super();
      }

      public void setPassword(String password) {
          this.password = password;
      }

      public String getPassword() {
          return password;
      }

      public void setUsername(String username) {
          this.username = username;
      }

      public String getUsername() {
          return username;
      }

      public void setFirstName(String firstName) {
          this.firstName = firstName;
      }

      public String getFirstName() {
          return firstName;
      }

      public void setLastName(String lastName) {
          this.lastName = lastName;
      }

      public String getLastName() {
          return lastName;
      }
    }

