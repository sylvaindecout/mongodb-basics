package test.sdc.mongodb;

import com.lordofthejars.nosqlunit.annotation.ShouldMatchDataSet;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.EmbeddedMongoInstancesFactory;
import com.lordofthejars.nosqlunit.mongodb.InMemoryMongoDb;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import test.sdc.model.CenterReference;
import test.sdc.model.Vessel;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.lordofthejars.nosqlunit.mongodb.InMemoryMongoDb.InMemoryMongoRuleBuilder.newInMemoryMongoDbRule;
import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule;
import static org.assertj.core.api.Assertions.assertThat;
import static test.sdc.model.VisibilityType.ALL_CENTERS;

@UsingDataSet(locations = "initial.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
public class VesselServiceIT {

    private static final String DB_NAME = "test";

    @ClassRule
    public static InMemoryMongoDb inMemoryMongoDb = newInMemoryMongoDbRule().build();
    private final MongoClient mongo = (MongoClient) EmbeddedMongoInstancesFactory.getInstance().getDefaultEmbeddedInstance();
    private final VesselService service = new VesselService(vesselsCollection());
    @Rule
    public MongoDbRule embeddedMongoDbRule = newMongoDbRule().defaultEmbeddedMongoDb(DB_NAME);

    private MongoCollection<Document> vesselsCollection() {
        return this.mongo.getDatabase(DB_NAME).getCollection("Vessel");
    }

    @Test
    @ShouldMatchDataSet
    public void should_add_vessel() {
        final String[] tables = {"vessels", "vessels_by_uuid"};
        final Vessel inputVessel = Vessel.newInstance()
                .withName("Le_Name")
                .withCategory("Cargo")
                .withVisibility(ALL_CENTERS)
                .withCreationCenter("Le_center")
                .build();

        this.service.add(inputVessel);
    }

    @Ignore
    @Test
    public void should_add_vessel_on_update_if_absent() {
        throw new RuntimeException("Not implemented yet!");
    }

    @Ignore
    @Test
    public void should_update_vessel() {
        throw new RuntimeException("Not implemented yet!");
    }

    @Ignore
    @Test
    public void should_remove_vessel() {
        throw new RuntimeException("Not implemented yet!");
    }

    @Ignore
    @Test
    public void should_do_nothing_on_remove_if_absent() {
        throw new RuntimeException("Not implemented yet!");
    }

    @Test
    public void should_expose_vessels_depending_on_visibility() {
        final CenterReference center = CenterReference.of("1");

        final List<Vessel> actual = this.service.findAll(center);

        final List<String> actualNames = actual.stream()
                .map(Vessel::getName)
                .collect(Collectors.toList());
        assertThat(actualNames).containsExactly("GLOBAL_1", "GLOBAL_2", "LOCAL_1");
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
    public void should_return_empty_list_when_there_is_no_vessel() {
        final CenterReference center = CenterReference.of("123");

        final List<Vessel> actual = this.service.findAll(center);

        assertThat(actual).isEmpty();
    }

    @Test
    public void should_not_find_vessel_from_absent_UUID() {
        final String uuid = "2549";

        final Optional<Vessel> actual = this.service.find(uuid);

        assertThat(actual).isEmpty();
    }

    @Test
    public void should_find_vessel_by_UUID() {
        final String uuid = "1";

        final Optional<Vessel> actual = this.service.find(uuid);

        final Optional<String> actualName = actual
                .map(Vessel::getName);
        assertThat(actualName).contains("GLOBAL_2");
    }

    @Ignore
    @Test
    public void should_filter_list_of_visible_vessels_by_name_fragment() {
        throw new RuntimeException("Not implemented yet!");
    }

    @Ignore
    @Test
    public void should_filter_list_of_visible_vessels_by_category() {
        throw new RuntimeException("Not implemented yet!");
    }

    @Ignore
    @Test
    public void should_expose_list_of_vessels_that_departed_recently_by_port() {
        throw new RuntimeException("Not implemented yet!");
    }

}