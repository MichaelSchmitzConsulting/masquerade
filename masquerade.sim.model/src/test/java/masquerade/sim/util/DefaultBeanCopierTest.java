package masquerade.sim.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * Test case for {@link DefaultBeanCopier}
 */
public class DefaultBeanCopierTest {
	@Test
	public void testCopyBean() {
		BeanCopier cloner = new DefaultBeanCopier();
		
		TestBean nestedBean = new TestBean();
		TestBean source = new TestBean(nestedBean);
		Object result = cloner.copyBean(source);
		
		assertEquals(source.toString(), result.toString());
		assertEquals(source, result);
	}
	
	public static class TestBean { 
		private int someInt = 505;
		private double someDouble = 60.6;
		private Double someBoxedDouble = new Double(90.9);
		private String someString = "303";
		private Integer someBoxedInteger = new Integer(42);
		private Collection<String> someStrings = new ArrayList<String>();
		private TestBean nestedBean;
		private Map<String, TestBean> map = new HashMap<String, TestBean>();
		private Set<TestBean> set = new HashSet<TestBean>();
		private Map<String, TestBean> linkedMap = new LinkedHashMap<String, TestBean>();
		private Set<TestBean> linkedSet = new LinkedHashSet<TestBean>();
		private Class<?> someClass = Object.class;
		
		/** Constructor for nested bean, does not recursively TestBean */
		public TestBean() {
			nestedBean = null;
		}

		/** Constructor for root bean, contains nested TestBean */
		public TestBean(TestBean nestedBean) {
			this.nestedBean = nestedBean;
			map.put("abc", new TestBean());
			map.put("def", new TestBean());
			set.add(new TestBean());

			linkedMap.put("def", new TestBean());
			linkedMap.put("abc", new TestBean());
			linkedMap.put("123", new TestBean());
			linkedSet.add(new TestBean());
			linkedSet.add(new TestBean());
		}
		
		public String getSomeReadOnlyValue() {
			return "xyz";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((linkedMap == null) ? 0 : linkedMap.hashCode());
			result = prime * result + ((linkedSet == null) ? 0 : linkedSet.hashCode());
			result = prime * result + ((map == null) ? 0 : map.hashCode());
			result = prime * result + ((nestedBean == null) ? 0 : nestedBean.hashCode());
			result = prime * result + ((set == null) ? 0 : set.hashCode());
			result = prime * result + ((someBoxedDouble == null) ? 0 : someBoxedDouble.hashCode());
			result = prime * result + ((someBoxedInteger == null) ? 0 : someBoxedInteger.hashCode());
			result = prime * result + ((someClass == null) ? 0 : someClass.hashCode());
			long temp;
			temp = Double.doubleToLongBits(someDouble);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			result = prime * result + someInt;
			result = prime * result + ((someString == null) ? 0 : someString.hashCode());
			result = prime * result + ((someStrings == null) ? 0 : someStrings.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TestBean other = (TestBean) obj;
			if (linkedMap == null) {
				if (other.linkedMap != null)
					return false;
			} else if (!linkedMap.equals(other.linkedMap))
				return false;
			if (linkedSet == null) {
				if (other.linkedSet != null)
					return false;
			} else if (!linkedSet.equals(other.linkedSet))
				return false;
			if (map == null) {
				if (other.map != null)
					return false;
			} else if (!map.equals(other.map))
				return false;
			if (nestedBean == null) {
				if (other.nestedBean != null)
					return false;
			} else if (!nestedBean.equals(other.nestedBean))
				return false;
			if (set == null) {
				if (other.set != null)
					return false;
			} else if (!set.equals(other.set))
				return false;
			if (someBoxedDouble == null) {
				if (other.someBoxedDouble != null)
					return false;
			} else if (!someBoxedDouble.equals(other.someBoxedDouble))
				return false;
			if (someBoxedInteger == null) {
				if (other.someBoxedInteger != null)
					return false;
			} else if (!someBoxedInteger.equals(other.someBoxedInteger))
				return false;
			if (someClass == null) {
				if (other.someClass != null)
					return false;
			} else if (!someClass.equals(other.someClass))
				return false;
			if (Double.doubleToLongBits(someDouble) != Double.doubleToLongBits(other.someDouble))
				return false;
			if (someInt != other.someInt)
				return false;
			if (someString == null) {
				if (other.someString != null)
					return false;
			} else if (!someString.equals(other.someString))
				return false;
			if (someStrings == null) {
				if (other.someStrings != null)
					return false;
			} else if (!someStrings.equals(other.someStrings))
				return false;
			return true;
		}

		public int getSomeInt() {
			return someInt;
		}

		public void setSomeInt(int someInt) {
			this.someInt = someInt;
		}

		public double getSomeDouble() {
			return someDouble;
		}

		public void setSomeDouble(double someDouble) {
			this.someDouble = someDouble;
		}

		public Double getSomeBoxedDouble() {
			return someBoxedDouble;
		}

		public void setSomeBoxedDouble(Double someBoxedDouble) {
			this.someBoxedDouble = someBoxedDouble;
		}

		public String getSomeString() {
			return someString;
		}

		public void setSomeString(String someString) {
			this.someString = someString;
		}

		public Integer getSomeBoxedInteger() {
			return someBoxedInteger;
		}

		public void setSomeBoxedInteger(Integer someBoxedInteger) {
			this.someBoxedInteger = someBoxedInteger;
		}

		public Collection<String> getSomeStrings() {
			return someStrings;
		}

		public void setSomeStrings(Collection<String> someStrings) {
			this.someStrings = someStrings;
		}

		public TestBean getNestedBean() {
			return nestedBean;
		}

		public void setNestedBean(TestBean nestedBean) {
			this.nestedBean = nestedBean;
		}

		public Map<String, TestBean> getMap() {
			return map;
		}

		public void setMap(Map<String, TestBean> map) {
			this.map = map;
		}

		public Set<TestBean> getSet() {
			return set;
		}

		public void setSet(Set<TestBean> set) {
			this.set = set;
		}

		public Map<String, TestBean> getLinkedMap() {
			return linkedMap;
		}

		public void setLinkedMap(Map<String, TestBean> linkedMap) {
			this.linkedMap = linkedMap;
		}

		public Set<TestBean> getLinkedSet() {
			return linkedSet;
		}

		public void setLinkedSet(Set<TestBean> linkedSet) {
			this.linkedSet = linkedSet;
		}

		public Class<?> getSomeClass() {
			return someClass;
		}

		public void setSomeClass(Class<?> someClass) {
			this.someClass = someClass;
		}
	}
}
