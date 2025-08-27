package com.example.springbuilderexample.builder.sql;

import java.util.StringJoiner;

/**
 * <h2>SQL - A Type-Safe, Constrained SQL Builder</h2>
 * <p>
 * This class is the entry point for building simple SQL statements (SELECT, UPDATE, DELETE).
 * It demonstrates a more advanced builder pattern that uses interfaces to enforce
 * a specific sequence of method calls for each type of statement.
 * </p>
 * <p>
 * This ensures that the user of the builder cannot construct an invalid query
 * by calling methods in the wrong order.
 * </p>
 */
public final class SQL {

    // --- Builder Entry Points ---

    public static SelectStep select(String... columns) {
        return new SQLBuilder().select(columns);
    }

    public static UpdateStep update(String table) {
        return new SQLBuilder().update(table);
    }

    public static DeleteFromStep deleteFrom(String table) {
        return new SQLBuilder().deleteFrom(table);
    }


    // --- Step Interfaces ---

    // --- Common Interfaces ---
    public interface BuildStep {
        String build();
    }

    public interface WhereStep extends BuildStep {
    }

    // --- SELECT Statement Interfaces ---
    public interface SelectStep {
        FromStep from(String table);
    }

    public interface FromStep extends BuildStep {
        WhereStep where(String condition);
    }

    // --- UPDATE Statement Interfaces ---
    public interface UpdateStep {
        SetStep set(String... assignments);
    }

    public interface SetStep extends BuildStep {
        WhereStep where(String condition);
    }

    // --- DELETE Statement Interfaces ---
    public interface DeleteFromStep extends BuildStep {
        WhereStep where(String condition);
    }

    /**
     * <h2>SQLBuilder - The Private Implementation</h2>
     * <p>
     * This is the concrete implementation of all the step interfaces. It holds the
     * state of the query being built. It's private, so the user only interacts
     * with it through the interfaces, which is what enforces the build order.
     * </p>
     */
    private static class SQLBuilder implements SelectStep, FromStep, UpdateStep, SetStep, DeleteFromStep, WhereStep {
        private enum StatementType { SELECT, UPDATE, DELETE }
        private StatementType statementType;

        private String table;
        private String whereCondition;
        private String[] columns;
        private String[] assignments;


        // --- SELECT specific methods ---
        public SelectStep select(String... columns) {
            this.statementType = StatementType.SELECT;
            this.columns = columns;
            return this;
        }

        @Override
        public FromStep from(String table) {
            this.table = table;
            return this;
        }

        // --- UPDATE specific methods ---
        public UpdateStep update(String table) {
            this.statementType = StatementType.UPDATE;
            this.table = table;
            return this;
        }

        @Override
        public SetStep set(String... assignments) {
            this.assignments = assignments;
            return this;
        }

        // --- DELETE specific methods ---
        public DeleteFromStep deleteFrom(String table) {
            this.statementType = StatementType.DELETE;
            this.table = table;
            return this;
        }

        // --- Common methods ---
        @Override
        public WhereStep where(String condition) {
            this.whereCondition = condition;
            return this;
        }

        @Override
        public String build() {
            switch (statementType) {
                case SELECT:
                    return buildSelect();
                case UPDATE:
                    return buildUpdate();
                case DELETE:
                    return buildDelete();
                default:
                    throw new IllegalStateException("Statement type not set.");
            }
        }

        private String buildSelect() {
            StringJoiner sql = new StringJoiner(" ");
            sql.add("SELECT").add(String.join(", ", columns));
            sql.add("FROM").add(table);
            if (whereCondition != null && !whereCondition.trim().isEmpty()) {
                sql.add("WHERE").add(whereCondition);
            }
            return sql.toString();
        }

        private String buildUpdate() {
            StringJoiner sql = new StringJoiner(" ");
            sql.add("UPDATE").add(table);
            sql.add("SET").add(String.join(", ", assignments));
            if (whereCondition != null && !whereCondition.trim().isEmpty()) {
                sql.add("WHERE").add(whereCondition);
            }
            return sql.toString();
        }

        private String buildDelete() {
            StringJoiner sql = new StringJoiner(" ");
            sql.add("DELETE FROM").add(table);
            if (whereCondition != null && !whereCondition.trim().isEmpty()) {
                sql.add("WHERE").add(whereCondition);
            }
            return sql.toString();
        }
    }
}
