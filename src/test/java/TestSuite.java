/*
 * The copyright of this file belongs to Koninklijke Philips N.V., 2019.
 */

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import com.philips.bootcamp.dal.ProjectDAOImplTest;
import com.philips.bootcamp.domain.ProjectTest;
import com.philips.bootcamp.rest.ProjectControllerTest;
import com.philips.bootcamp.service.ProjectServiceImplTest;
import com.philips.bootcamp.tools.CheckstyleTest;
import com.philips.bootcamp.tools.MavenTest;
import com.philips.bootcamp.tools.PmdTest;
import com.philips.bootcamp.utils.FileUtilsTest;
import com.philips.bootcamp.utils.GenericUtilsTest;
import com.philips.bootcamp.utils.StreamUtilsTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  ProjectDAOImplTest.class,
  ProjectTest.class,
  ProjectControllerTest.class,
  ProjectServiceImplTest.class,
  CheckstyleTest.class,
  MavenTest.class,
  PmdTest.class,
  FileUtilsTest.class,
  GenericUtilsTest.class,
  StreamUtilsTest.class
})
public class TestSuite{

}
