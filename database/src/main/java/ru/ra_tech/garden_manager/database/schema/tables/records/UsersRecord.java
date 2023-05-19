/*
 * This file is generated by jOOQ.
 */
package ru.ra_tech.garden_manager.database.schema.tables.records;


import java.time.OffsetDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Row10;
import org.jooq.impl.UpdatableRecordImpl;

import ru.ra_tech.garden_manager.database.schema.tables.Users;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class UsersRecord extends UpdatableRecordImpl<UsersRecord> implements Record10<Integer, String, String, String, String, String, Boolean, OffsetDateTime, OffsetDateTime, OffsetDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.users.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.users.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.users.login</code>.
     */
    public void setLogin(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.users.login</code>.
     */
    public String getLogin() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.users.name</code>.
     */
    public void setName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.users.name</code>.
     */
    public String getName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.users.email</code>.
     */
    public void setEmail(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.users.email</code>.
     */
    public String getEmail() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.users.password</code>.
     */
    public void setPassword(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.users.password</code>.
     */
    public String getPassword() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.users.tokenId</code>.
     */
    public void setTokenid(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.users.tokenId</code>.
     */
    public String getTokenid() {
        return (String) get(5);
    }

    /**
     * Setter for <code>public.users.deleted</code>.
     */
    public void setDeleted(Boolean value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.users.deleted</code>.
     */
    public Boolean getDeleted() {
        return (Boolean) get(6);
    }

    /**
     * Setter for <code>public.users.createdAt</code>.
     */
    public void setCreatedat(OffsetDateTime value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.users.createdAt</code>.
     */
    public OffsetDateTime getCreatedat() {
        return (OffsetDateTime) get(7);
    }

    /**
     * Setter for <code>public.users.updatedAt</code>.
     */
    public void setUpdatedat(OffsetDateTime value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.users.updatedAt</code>.
     */
    public OffsetDateTime getUpdatedat() {
        return (OffsetDateTime) get(8);
    }

    /**
     * Setter for <code>public.users.deletedAt</code>.
     */
    public void setDeletedat(OffsetDateTime value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.users.deletedAt</code>.
     */
    public OffsetDateTime getDeletedat() {
        return (OffsetDateTime) get(9);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record10 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row10<Integer, String, String, String, String, String, Boolean, OffsetDateTime, OffsetDateTime, OffsetDateTime> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    @Override
    public Row10<Integer, String, String, String, String, String, Boolean, OffsetDateTime, OffsetDateTime, OffsetDateTime> valuesRow() {
        return (Row10) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return Users.USERS.ID;
    }

    @Override
    public Field<String> field2() {
        return Users.USERS.LOGIN;
    }

    @Override
    public Field<String> field3() {
        return Users.USERS.NAME;
    }

    @Override
    public Field<String> field4() {
        return Users.USERS.EMAIL;
    }

    @Override
    public Field<String> field5() {
        return Users.USERS.PASSWORD;
    }

    @Override
    public Field<String> field6() {
        return Users.USERS.TOKENID;
    }

    @Override
    public Field<Boolean> field7() {
        return Users.USERS.DELETED;
    }

    @Override
    public Field<OffsetDateTime> field8() {
        return Users.USERS.CREATEDAT;
    }

    @Override
    public Field<OffsetDateTime> field9() {
        return Users.USERS.UPDATEDAT;
    }

    @Override
    public Field<OffsetDateTime> field10() {
        return Users.USERS.DELETEDAT;
    }

    @Override
    public Integer component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getLogin();
    }

    @Override
    public String component3() {
        return getName();
    }

    @Override
    public String component4() {
        return getEmail();
    }

    @Override
    public String component5() {
        return getPassword();
    }

    @Override
    public String component6() {
        return getTokenid();
    }

    @Override
    public Boolean component7() {
        return getDeleted();
    }

    @Override
    public OffsetDateTime component8() {
        return getCreatedat();
    }

    @Override
    public OffsetDateTime component9() {
        return getUpdatedat();
    }

    @Override
    public OffsetDateTime component10() {
        return getDeletedat();
    }

    @Override
    public Integer value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getLogin();
    }

    @Override
    public String value3() {
        return getName();
    }

    @Override
    public String value4() {
        return getEmail();
    }

    @Override
    public String value5() {
        return getPassword();
    }

    @Override
    public String value6() {
        return getTokenid();
    }

    @Override
    public Boolean value7() {
        return getDeleted();
    }

    @Override
    public OffsetDateTime value8() {
        return getCreatedat();
    }

    @Override
    public OffsetDateTime value9() {
        return getUpdatedat();
    }

    @Override
    public OffsetDateTime value10() {
        return getDeletedat();
    }

    @Override
    public UsersRecord value1(Integer value) {
        setId(value);
        return this;
    }

    @Override
    public UsersRecord value2(String value) {
        setLogin(value);
        return this;
    }

    @Override
    public UsersRecord value3(String value) {
        setName(value);
        return this;
    }

    @Override
    public UsersRecord value4(String value) {
        setEmail(value);
        return this;
    }

    @Override
    public UsersRecord value5(String value) {
        setPassword(value);
        return this;
    }

    @Override
    public UsersRecord value6(String value) {
        setTokenid(value);
        return this;
    }

    @Override
    public UsersRecord value7(Boolean value) {
        setDeleted(value);
        return this;
    }

    @Override
    public UsersRecord value8(OffsetDateTime value) {
        setCreatedat(value);
        return this;
    }

    @Override
    public UsersRecord value9(OffsetDateTime value) {
        setUpdatedat(value);
        return this;
    }

    @Override
    public UsersRecord value10(OffsetDateTime value) {
        setDeletedat(value);
        return this;
    }

    @Override
    public UsersRecord values(Integer value1, String value2, String value3, String value4, String value5, String value6, Boolean value7, OffsetDateTime value8, OffsetDateTime value9, OffsetDateTime value10) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached UsersRecord
     */
    public UsersRecord() {
        super(Users.USERS);
    }

    /**
     * Create a detached, initialised UsersRecord
     */
    public UsersRecord(Integer id, String login, String name, String email, String password, String tokenid, Boolean deleted, OffsetDateTime createdat, OffsetDateTime updatedat, OffsetDateTime deletedat) {
        super(Users.USERS);

        setId(id);
        setLogin(login);
        setName(name);
        setEmail(email);
        setPassword(password);
        setTokenid(tokenid);
        setDeleted(deleted);
        setCreatedat(createdat);
        setUpdatedat(updatedat);
        setDeletedat(deletedat);
        resetChangedOnNotNull();
    }
}
