package io.github.jkroepke.keycloak.extension;

import com.azure.resourcemanager.msi.MsiManager;
import com.azure.resourcemanager.msi.models.Identity;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.KeycloakSession;

import java.util.HashMap;
import java.util.Map;

@JBossLog
public class UserEventListenerProvider implements EventListenerProvider {

    private final MsiManager manager;
    private final KeycloakSession keycloakSession;

    public UserEventListenerProvider(KeycloakSession keycloakSession, MsiManager manager) {
        this.manager = manager;
        this.keycloakSession = keycloakSession;
    }

    public void onEvent(Event event) {
        log.infof("onEvent event=%s type=%s realm=%suserId=%s", event, event.getType(), event.getRealmId(), event.getUserId());
    }

    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        ResourceType resourceType = event.getResourceType();
        OperationType operationType = event.getOperationType();

        if (operationType == OperationType.CREATE && resourceType == ResourceType.USER) {
            log.infof("Attempt to create managed identity for user %s ...", event.getRepresentation());

            Map<String, String> tags = new HashMap<String, String>();
            tags.put("user", event.getAuthDetails().getUserId());
            tags.put("resource-path", event.getResourcePath());
            tags.put("creation-date", String.valueOf(event.getTime()));

            Identity userIdentity = manager.identities().define("id-joe-westeu-001").withRegion("westeurope").withExistingResourceGroup("rg-keycloak-westeu").withTags(tags).create();
            // userIdentity.manager().serviceClient().getFederatedIdentityCredentials().createOrUpdate()
            log.infof("User identity is %s ...", userIdentity.toString());
        } else {
            log.infof("onEvent adminEvent=%s type=%s resourceType=%s resourcePath=%s includeRepresentation=%s", event, event.getOperationType(), event.getResourceType(), event.getResourcePath(), includeRepresentation);
        }
    }

    public void close() {

    }
}
