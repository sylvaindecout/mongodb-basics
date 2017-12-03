package test.sdc.mongodb;

import org.bson.Document;
import test.sdc.model.Vessel;
import test.sdc.model.VisibilityType;

import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static test.sdc.model.VisibilityType.ALL_CENTERS;
import static test.sdc.model.VisibilityType.CREATION_CENTER_ONLY;

/**
 * Convert objects between domain and MongoDB data models.
 */
final class VesselConverter {

    public static final String ID = "_id";
    public static final String VISIBILITY = "visibility";
    public static final String CREATION_CENTER = "creation";
    public static final String GLOBAL_VISIBILITY = "_ALL";

    private static final String NAME = "name";
    private static final String CATEGORY = "category";

    /**
     * Private constructor.
     */
    private VesselConverter() {
    }

    /**
     * Transform MongoDB object into domain object.
     *
     * @param document MongoDB object
     * @return domain object
     * @throws NullPointerException mandatory argument is missing
     */
    public static Vessel fromJson(final Document document) {
        requireNonNull(document, "Input object is missing");
        return Vessel.fromUuid(String.valueOf(document.get(ID)))
                .withName(String.valueOf(document.get(NAME)))
                .withCategory(String.valueOf(document.get(CATEGORY)))
                .withCreationCenter(String.valueOf(document.get(CREATION_CENTER)))
                .withVisibility(readVisibility(document))
                .build();
    }

    /**
     * Transform domain object into MongoDB object.
     *
     * @param uuid   unique object identifier
     * @param vessel domain object
     * @return MongoDB object
     * @throws NullPointerException mandatory argument is missing
     */
    public static Document toJson(final String uuid, final Vessel vessel) {
        requireNonNull(uuid, "UUID is missing");
        requireNonNull(vessel, "Input object is missing");
        return new Document()
                .append(ID, uuid)
                .append(NAME, vessel.getName())
                .append(CATEGORY, String.valueOf(vessel.getCategory().getUuid()))
                .append(CREATION_CENTER, String.valueOf(vessel.getCreationCenter()))
                .append(VISIBILITY, generateVisibility(vessel));
    }

    /**
     * Generate visibility field, so that it includes creation center information if necessary, in order to be used as an index.
     *
     * @param vessel domain object
     * @return visibility field
     */
    private static String generateVisibility(final Vessel vessel) {
        return vessel.getVisibility() == ALL_CENTERS
                ? GLOBAL_VISIBILITY
                : vessel.getCreationCenter().getUuid();
    }

    /**
     * Read visibility type from input MongoDB object.
     *
     * @param document MongoDB object
     * @return visibility type
     */
    private static VisibilityType readVisibility(final Document document) {
        final Object field = document.get(VISIBILITY);
        return Objects.equals(field, GLOBAL_VISIBILITY) ? ALL_CENTERS : CREATION_CENTER_ONLY;
    }

}