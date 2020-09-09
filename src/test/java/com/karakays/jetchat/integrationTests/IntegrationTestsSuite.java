package com.karakays.jetchat.integrationTests;

import com.karakays.jetchat.integrationTests.authentication.api.AuthenticationControllerTest;
import com.karakays.jetchat.integrationTests.authentication.domain.service.DefaultUserServiceTest;
import com.karakays.jetchat.integrationTests.chatroom.api.ChatRoomControllerTest;
import com.karakays.jetchat.integrationTests.chatroom.domain.service.CassandraInstantMessageServiceTest;
import com.karakays.jetchat.integrationTests.chatroom.domain.service.RedisChatRoomServiceTest;
import com.karakays.jetchat.integrationTests.test.AbstractIntegrationTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  CassandraInstantMessageServiceTest.class,
  RedisChatRoomServiceTest.class,
  DefaultUserServiceTest.class,
  AuthenticationControllerTest.class,
  ChatRoomControllerTest.class
})
public class IntegrationTestsSuite extends AbstractIntegrationTest {

}
