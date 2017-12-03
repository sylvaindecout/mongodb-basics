package test.sdc.mongodb;

import com.mongodb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.sdc.model.CenterReference;
import test.sdc.model.Vessel;

import java.util.*;

import static test.sdc.mongodb.VesselConverter.*;

/**
 * Interface of vessel service with MongoDB.
 */
public final class VesselService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VesselService.class);

    private final DBCollection vesselsCollection;

    /**
     * Constructor.
     *
     * @param vesselsCollection collection of MongoDB documents for vessels
     */
    public VesselService(final DBCollection vesselsCollection) {
        this.vesselsCollection = vesselsCollection;
    }

    /**
     * Get list of all vessels that are visible to site.
     *
     * @param center center
     * @return list of all visible vessels
     */
    public List<Vessel> findAll(final CenterReference center) {
        LOGGER.trace("Find vessels by site ID: {}", center);
        final List<Vessel> res = new ArrayList<>();
        final DBObject query = BasicDBObjectBuilder.start()
                .add("$or", Arrays.asList(
                        new BasicDBObject(VISIBILITY, GLOBAL_VISIBILITY),
                        new BasicDBObject(VISIBILITY, center.getUuid())))
                .get();
        final DBCursor cursor = this.vesselsCollection.find(query);
        while (cursor.hasNext()) {
            final DBObject dbObject = cursor.next();
            res.add(fromJson(dbObject));
        }
        LOGGER.trace("Found {} match(es) for center={}", res.size(), center);
        return res;
    }

    /**
     * Get vessel from selected UUID.
     *
     * @param uuid UUID
     * @return vessel
     */
    public Optional<Vessel> find(final String uuid) {
        LOGGER.trace("Find vessel by UUID '{}'", uuid);
        final DBObject query = BasicDBObjectBuilder.start()
                .add(ID, uuid)
                .get();
        final DBObject dbObject = this.vesselsCollection.findOne(query);
        LOGGER.trace("Found {}match for vessel ID={}", dbObject == null ? "no " : "", uuid);
        return dbObject == null
                ? Optional.empty()
                : Optional.of(fromJson(dbObject));
    }

    /**
     * Create new vessel with input data.
     *
     * @param vessel vessel
     */
    public void add(final Vessel vessel) {
        LOGGER.trace("Create {}", vessel);
        final String uuid = UUID.randomUUID().toString();
        final DBObject dbObject = toJson(uuid, vessel);
        this.vesselsCollection.insert(dbObject);
        LOGGER.trace("Creation of vessel {} completed", vessel);
    }

}