package org.fossasia.openevent.app.unit.utils.misc;

import org.fossasia.openevent.app.common.app.ContextManager;
import org.fossasia.openevent.app.common.data.models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.context.Context;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ContextManagerTest {

    @Rule public MockitoRule rule = MockitoJUnit.rule();
    @Mock private SentryClient client;
    @Mock private Context context;

    private SentryClient original;

    private ContextManager contextManager;

    @Before
    public void setUp() {
        contextManager = new ContextManager();
        original = Sentry.getStoredClient();
        Sentry.setStoredClient(client);
    }

    @After
    public void tearDown() {
        Sentry.setStoredClient(original);
    }

    @Test
    public void shouldClearContext() {
        contextManager.clearOrganiser();
        verify(client).clearContext();
    }

    @Test
    public void shouldSetOrganiser() {
        when(client.getContext()).thenReturn(context);
        User user = new User("test@example.com");
        user.setId(32);

        Map<String, Object> data = new ConcurrentHashMap<>();
        data.put("details", user);

        contextManager.setOrganiser(user);
        ArgumentCaptor<io.sentry.event.User> argument = ArgumentCaptor.forClass(io.sentry.event.User.class);
        verify(context).setUser(argument.capture());

        assertEquals("test@example.com", argument.getValue().getEmail());
        assertEquals("32", argument.getValue().getId());
        assertEquals(data, argument.getValue().getData());
    }

}
