package com.kienht.cache.features.employee;

import com.kienht.cache.PrefUtils;
import com.kienht.cache.database.RoomDB;
import com.kienht.cache.mapper.employee.EmployeeCacheMapper;
import com.kienht.data.model.EmployeeData;
import com.kienht.data.repository.employee.EmployeeCache;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;

/**
 * Note:
 * Created by kienht on 5/2/18.
 */
public class EmployeeCacheImpl implements EmployeeCache {

    public static final String TAG = EmployeeCacheImpl.class.getSimpleName();

    private final long EXPIRATION_TIME = 60 * 10 * 1000;

    private RoomDB roomDB;
    private EmployeeCacheMapper mapper;
    private PrefUtils prefUtils;
    private Scheduler schedulerComputation;

    @Inject
    public EmployeeCacheImpl(RoomDB roomDB, EmployeeCacheMapper mapper, PrefUtils prefUtils, Scheduler schedulerComputation) {
        this.roomDB = roomDB;
        this.mapper = mapper;
        this.prefUtils = prefUtils;
        this.schedulerComputation = schedulerComputation;
    }

    @Override
    public Completable saveEmployees(List<EmployeeData> employees) {
        return Observable.fromIterable(employees)
                .map(employeeEntity -> mapper.mapToCached(employeeEntity))
                .toList()
                .toFlowable()
                .flatMapCompletable(employeeCacheds -> {
                    roomDB.employeeDAO().insert(employeeCacheds);
                    return Completable.complete();
                })
                .subscribeOn(schedulerComputation);
    }

    @Override
    public Flowable<List<EmployeeData>> getEmployees() {
        return roomDB.employeeDAO().getEmployees()
                .flatMapPublisher(employeeCacheds -> Flowable.fromIterable(employeeCacheds)
                        .map(employeeCached -> mapper.mapFromCached(employeeCached))
                        .toList()
                        .toFlowable())
                .subscribeOn(schedulerComputation);
    }

    @Override
    public Single<Boolean> isCache() {
        return Single.defer(() -> Single.just(!roomDB.employeeDAO().getEmployees().isEmpty().blockingGet()))
                .subscribeOn(schedulerComputation);
    }

    @Override
    public void setLastCacheTime(long lastCacheTime) {
        prefUtils.set(PrefUtils.PREF_KEY.PREF_KEY_LAST_CACHE, lastCacheTime);
    }

    @Override
    public boolean isExpired() {
        long currentTime = System.currentTimeMillis();
        long lastCacheUpdateTime = prefUtils.get(PrefUtils.PREF_KEY.PREF_KEY_LAST_CACHE, 0L);
        return currentTime - lastCacheUpdateTime > EXPIRATION_TIME;
    }
}
