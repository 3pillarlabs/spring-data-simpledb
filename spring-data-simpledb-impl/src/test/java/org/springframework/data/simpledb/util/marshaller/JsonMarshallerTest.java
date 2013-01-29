package org.springframework.data.simpledb.util.marshaller;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JsonMarshallerTest {

    private JsonMarshaller cut;

    @Before
    public void setUp() {
        cut = new JsonMarshaller();
    }

    @Test
    public void unmarshal_should_properly_unmarshal_a_json_string_into_the_target_class_instance() throws IOException {

        // Prepare
        String userJsonString = new String(IOUtils.toByteArray(this.getClass().getClassLoader().getResourceAsStream(
                "org/springframework/data/simpledb/util/marshaller/user.json")));
        assertNotNull(userJsonString);

        // Exercise
        User expectedUser = cut.unmarshal(userJsonString, User.class);

        // Verify
        assertNotNull(expectedUser);
        assertEquals(User.Gender.MALE, expectedUser.getGender());
        assertEquals("Joe", expectedUser.getName().getFirst());
        assertEquals("Sixpack", expectedUser.getName().getLast());
        assertNotNull(expectedUser.getUserImage());
    }

    @Test
    public void unmarshal_should_unmarshal_a_json_string_without_knowing_the_target_class_type() throws IOException {

        // Prepare
        String userJsonString = new String(IOUtils.toByteArray(this.getClass().getClassLoader().getResourceAsStream(
                "org/springframework/data/simpledb/util/marshaller/user_with_classinfo.json")));
        assertNotNull(userJsonString);

        // Exercise
        User returnedUser = (User) cut.unmarshal(userJsonString);

        // Verify
        assertNotNull(returnedUser);
        assertEquals(User.Gender.MALE, returnedUser.getGender());
        assertEquals("Joe", returnedUser.getName().getFirst());
        assertEquals("Sixpack", returnedUser.getName().getLast());
        assertNotNull(returnedUser.getUserImage());
    }

    @Test
    public void marshal_should_marshal_Object() throws IOException {

        // Prepare
        User newUser = new User();
        newUser.setName(new User.Name());
        newUser.getName().setFirst("Joe");
        newUser.getName().setLast("Sixpack");
        Object object = newUser;

        // Exercise
        String marshalledUser = cut.marshal(object);
        User returnedUser = (User) cut.unmarshal(marshalledUser);

        // Verify
        assertNotNull(returnedUser);
        assertEquals(newUser.getName().getFirst(), returnedUser.getName().getFirst());
        assertEquals(newUser.getName().getLast(), returnedUser.getName().getLast());
    }

}

class User {
    public enum Gender {MALE}

    public static class Name {
        private String _first;

        private String _last;

        public String getFirst() {
            return _first;
        }

        public String getLast() {
            return _last;
        }

        public void setFirst(String s) {
            _first = s;
        }

        public void setLast(String s) {
            _last = s;
        }
    }

    private Gender _gender;
    private Name _name;
    private boolean _isVerified;
    private byte[] _userImage;

    public Name getName() {
        return _name;
    }

    public Gender getGender() {
        return _gender;
    }

    public byte[] getUserImage() {
        return _userImage;
    }

    public void setName(Name n) {
        _name = n;
    }

}