package test.sdc.mongodb;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.sdc.model.CenterReference;
import test.sdc.model.Vessel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;
import static test.sdc.mongodb.VesselConverter.*;

/**
 * Interface of vessel service with MongoDB.
 */
public final class VesselService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VesselService.class);

    private final MongoCollection<Document> vesselsCollection;

    /**
     * Constructor.
     *
     * @param vesselsCollection collection of MongoDB documents for vessels
     */
    public VesselService(final MongoCollection<Document> vesselsCollection) {
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
        this.vesselsCollection
                .find(or(eq(VISIBILITY, GLOBAL_VISIBILITY), eq(VISIBILITY, center.getUuid())))
                .map(VesselConverter::fromJson)
                .into(res);
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
        final Vessel res = this.vesselsCollection.find(eq(ID, uuid))
                .map(VesselConverter::fromJson)
                .first();
        LOGGER.trace("Found {}match for vessel ID={}", res == null ? "no " : "", uuid);
        return Optional.ofNullable(res);
    }

    /**
     * Create new vessel with input data.
     *
     * @param vessel vessel
     */
    public void add(final Vessel vessel) {
        LOGGER.trace("Create {}", vessel);
        final String uuid = UUID.randomUUID().toString();
        final Document document = toJson(uuid, vessel);
        this.vesselsCollection.insertOne(document);
        LOGGER.trace("Creation of vessel {} completed", vessel);
    }

}