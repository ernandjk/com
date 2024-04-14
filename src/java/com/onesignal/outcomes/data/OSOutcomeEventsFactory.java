package com.onesignal.outcomes.data;

import kotlin.jvm.internal.Intrinsics;
import com.onesignal.OSSharedPreferences;
import com.onesignal.OneSignalDb;
import com.onesignal.outcomes.domain.OSOutcomeEventsRepository;
import com.onesignal.OSLogger;
import com.onesignal.OneSignalAPIClient;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B)\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\b\u0010\b\u001a\u0004\u0018\u00010\t¢\u0006\u0002\u0010\nJ\b\u0010\u000f\u001a\u00020\u0010H\u0002J\u0006\u0010\u0011\u001a\u00020\u000eJ\b\u0010\u0012\u001a\u00020\u000eH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0013" }, d2 = { "Lcom/onesignal/outcomes/data/OSOutcomeEventsFactory;", "", "logger", "Lcom/onesignal/OSLogger;", "apiClient", "Lcom/onesignal/OneSignalAPIClient;", "dbHelper", "Lcom/onesignal/OneSignalDb;", "preferences", "Lcom/onesignal/OSSharedPreferences;", "(Lcom/onesignal/OSLogger;Lcom/onesignal/OneSignalAPIClient;Lcom/onesignal/OneSignalDb;Lcom/onesignal/OSSharedPreferences;)V", "outcomeEventsCache", "Lcom/onesignal/outcomes/data/OSOutcomeEventsCache;", "repository", "Lcom/onesignal/outcomes/domain/OSOutcomeEventsRepository;", "createRepository", "Lcom/onesignal/outcomes/data/OSOutcomeEventsRepository;", "getRepository", "validateRepositoryVersion", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public final class OSOutcomeEventsFactory
{
    private final OneSignalAPIClient apiClient;
    private final OSLogger logger;
    private final OSOutcomeEventsCache outcomeEventsCache;
    private OSOutcomeEventsRepository repository;
    
    public OSOutcomeEventsFactory(final OSLogger logger, final OneSignalAPIClient apiClient, final OneSignalDb oneSignalDb, final OSSharedPreferences osSharedPreferences) {
        Intrinsics.checkNotNullParameter((Object)logger, "logger");
        Intrinsics.checkNotNullParameter((Object)apiClient, "apiClient");
        this.logger = logger;
        this.apiClient = apiClient;
        Intrinsics.checkNotNull((Object)oneSignalDb);
        Intrinsics.checkNotNull((Object)osSharedPreferences);
        this.outcomeEventsCache = new OSOutcomeEventsCache(logger, oneSignalDb, osSharedPreferences);
    }
    
    private final com.onesignal.outcomes.data.OSOutcomeEventsRepository createRepository() {
        com.onesignal.outcomes.data.OSOutcomeEventsRepository osOutcomeEventsRepository;
        if (this.outcomeEventsCache.isOutcomesV2ServiceEnabled()) {
            osOutcomeEventsRepository = new OSOutcomeEventsV2Repository(this.logger, this.outcomeEventsCache, new OSOutcomeEventsV2Service(this.apiClient));
        }
        else {
            osOutcomeEventsRepository = new OSOutcomeEventsV1Repository(this.logger, this.outcomeEventsCache, new OSOutcomeEventsV1Service(this.apiClient));
        }
        return osOutcomeEventsRepository;
    }
    
    private final OSOutcomeEventsRepository validateRepositoryVersion() {
        if (!this.outcomeEventsCache.isOutcomesV2ServiceEnabled()) {
            final OSOutcomeEventsRepository repository = this.repository;
            if (repository instanceof OSOutcomeEventsV1Repository) {
                Intrinsics.checkNotNull((Object)repository);
                return repository;
            }
        }
        if (this.outcomeEventsCache.isOutcomesV2ServiceEnabled()) {
            final OSOutcomeEventsRepository repository2 = this.repository;
            if (repository2 instanceof OSOutcomeEventsV2Repository) {
                Intrinsics.checkNotNull((Object)repository2);
                return repository2;
            }
        }
        return this.createRepository();
    }
    
    public final OSOutcomeEventsRepository getRepository() {
        OSOutcomeEventsRepository validateRepositoryVersion;
        if (this.repository != null) {
            validateRepositoryVersion = this.validateRepositoryVersion();
        }
        else {
            validateRepositoryVersion = this.createRepository();
        }
        return validateRepositoryVersion;
    }
}
