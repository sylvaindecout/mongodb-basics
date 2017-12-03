package test.sdc.mongodb;

import org.bson.Document;
import org.junit.Test;
import test.sdc.model.Vessel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static test.sdc.model.VisibilityType.ALL_CENTERS;
import static test.sdc.mongodb.VesselConverter.fromJson;
import static test.sdc.mongodb.VesselConverter.toJson;

public class VesselConverterTest {

    @Test
    public void should_not_lose_information() {
        final Vessel inputVessel = Vessel.fromUuid("2") //newInstance()
                .withName("Le_Name")
                .withCategory("Cargo")
                .withVisibility(ALL_CENTERS)
                .withCreationCenter("Le_center")
                .build();

        final Document document = toJson("2", inputVessel);
        final Vessel outputVessel = fromJson(document);

        assertThat(outputVessel).isEqualToComparingFieldByField(inputVessel);
    }

    @Test
    public void should_reject_null_vessels_IDs() {
        final Vessel inputVessel = Vessel.fromUuid("2") //newInstance()
                .withName("Le_Name")
                .withCategory("Cargo")
                .withVisibility(ALL_CENTERS)
                .withCreationCenter("Le_center")
                .build();

        assertThatNullPointerException().isThrownBy(() -> toJson(null, inputVessel));
    }

    @Test
    public void should_reject_null_vessels() {
        assertThatNullPointerException().isThrownBy(() -> toJson("1", null));
    }

    @Test
    public void should_reject_null_JSON() {
        assertThatNullPointerException().isThrownBy(() -> fromJson(null));
    }

}