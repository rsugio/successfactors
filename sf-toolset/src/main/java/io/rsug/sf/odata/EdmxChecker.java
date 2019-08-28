package io.rsug.sf.odata;

import org.apache.olingo.odata2.api.edm.FullQualifiedName;
import org.apache.olingo.odata2.api.edm.provider.*;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.core.edm.provider.EdmxProvider;

import java.io.InputStream;

public class EdmxChecker {
    public final EdmxProvider edmx;

    public EdmxChecker(InputStream is) throws ODataException {
        edmx = new EdmxProvider();
        edmx.parse(is, true);
        for (Schema x : edmx.getSchemas()) {
            assert x.getEntityContainers() != null;
            assert x.getNamespace() != null && x.getNamespace().length() > 0;
        }
    }

    public void analyze() throws ODataException {
        assert edmx != null;
        // two schemas, SFODataSet and SDOData
        // SFODataSet: Schema.entityContainers == 1
        // SFODataSet.entityContainers[0].entitySets = 652
        // SFODataSet.entityContainers[0].associationSets = 3631
        // SFODataSet.entityContainers[0].functionImports = 61
        // SFODataSet.entityContainers[0].name = "EntityContainer"

        // SFOData: Schema.entityTypes = 652
        // SFOData: Schema.complexTypes = 74
        // SFOData: Schema.associations = 3631
        // SFOData: Schema.entityContainers = 0
        for (Schema schema : edmx.getSchemas()) {
            for (EntityContainer ec : schema.getEntityContainers()) {
                for (EntitySet es : ec.getEntitySets()) {
                    String name = es.getName();
                    FullQualifiedName typeName = es.getEntityType();
                    ComplexType ctype = edmx.getComplexType(typeName);
                    EntityType etype = edmx.getEntityType(typeName);
                    assert etype != null || ctype != null : typeName;
//                    if (name.startsWith("FODivision")) {
//                        System.out.println(name + " => " + etype.getKey().getKeys());
//                    }
                }
                for (AssociationSet o : ec.getAssociationSets()) {

                }
            }
        }

    }
}
