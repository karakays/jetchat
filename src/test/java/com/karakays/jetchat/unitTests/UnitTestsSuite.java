package com.karakays.jetchat.unitTests;

import com.karakays.jetchat.unitTests.chatroom.domain.model.InstantMessageBuilderTest;
import com.karakays.jetchat.unitTests.chatroom.domain.service.RedisChatRoomServiceTest;
import com.karakays.jetchat.unitTests.utils.DestinationsTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.karakays.jetchat.unitTests.utils.SystemMessagesTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  InstantMessageBuilderTest.class,
  DestinationsTest.class,
  SystemMessagesTest.class,
  RedisChatRoomServiceTest.class
})
public class UnitTestsSuite {

}
