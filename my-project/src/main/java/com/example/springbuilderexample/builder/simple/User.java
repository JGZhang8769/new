package com.example.springbuilderexample.builder.simple;

/**
 * <h2>User - The Product Class</h2>
 * <p>
 * This is the complex object we want to create. It has several fields, some of
 * which are mandatory (firstName, lastName) and some are optional (age, phone, address).
 * </p>
 * <p>
 * The constructor is private, which means a User object can only be created
 * through its builder. This enforces the use of the builder pattern.
 * </p>
 */
public class User {

    // --- Fields ---
    // These are final because we want to create an immutable User object.
    private final String firstName; // Required
    private final String lastName;  // Required
    private final int age;          // Optional
    private final String phone;     // Optional
    private final String address;   // Optional

    /**
     * The private constructor that takes a UserBuilder object.
     * It initializes the User's fields from the builder's fields.
     *
     * @param builder The UserBuilder instance.
     */
    private User(UserBuilder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.age = builder.age;
        this.phone = builder.phone;
        this.address = builder.address;
    }

    // --- Getters ---
    // We only provide getters to make the User object immutable.
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "User: " + this.firstName + ", " + this.lastName + ", " + this.age + ", " + this.phone + ", " + this.address;
    }

    /**
     * <h2>UserBuilder - The Static Nested Builder Class</h2>
     * <p>
     * This is the heart of the Builder pattern. It's a static nested class within User.
     * </p>
     * <p>
     * It has the same fields as the User class. The builder's constructor takes
     * the required fields as arguments. Optional fields can be set using fluent
     * setter methods (e.g., age(), phone(), address()).
     * </p>
     * <p>
     * The build() method is the final step. It creates an instance of the User
     * class by calling its private constructor.
     * </p>
     */
    public static class UserBuilder {

        // --- Builder Fields ---
        private final String firstName; // Required
        private final String lastName;  // Required
        private int age;                // Optional
        private String phone;           // Optional
        private String address;         // Optional

        /**
         * The builder's constructor with the required fields.
         *
         * @param firstName The user's first name.
         * @param lastName  The user's last name.
         */
        public UserBuilder(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        /**
         * Sets the optional age.
         *
         * @param age The user's age.
         * @return The builder instance for method chaining.
         */
        public UserBuilder age(int age) {
            this.age = age;
            return this;
        }

        /**
         * Sets the optional phone number.
         *
         * @param phone The user's phone number.
         * @return The builder instance for method chaining.
         */
        public UserBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        /**
         * Sets the optional address.
         *
         * @param address The user's address.
         * @return The builder instance for method chaining.
         */
        public UserBuilder address(String address) {
            this.address = address;
            return this;
        }

        /**
         * The final build method. It creates and returns a User object.
         * <p>
         * This is also a good place to add validation for the object's state
         * before it's created.
         * </p>
         *
         * @return A new User object.
         */
        public User build() {
            User user = new User(this);
            // Example validation:
            if (user.getAge() > 120 || user.getAge() < 0) {
                throw new IllegalStateException("Age is not valid!");
            }
            return user;
        }
    }
}
