
package io.github.jkroepke.keycloak.extension;

import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.msi.MsiManager;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class UserEventListenerProviderFactory implements EventListenerProviderFactory {

    private MsiManager manager;
    private static final String PROVIDER_ID = "oidc-cloud-identities";

    public void init(Config.Scope scope) {
        AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);
        DefaultAzureCredential defaultCredential = new DefaultAzureCredentialBuilder().build();
        manager = MsiManager.authenticate(defaultCredential, profile);
    }

    public EventListenerProvider create(KeycloakSession keycloakSession) {
        return new UserEventListenerProvider(keycloakSession, manager);
    }

    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    public void close() {

    }

    public String getId() {
        return PROVIDER_ID;
    }
}
