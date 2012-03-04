package masquerade.sim.model;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for Masquerade Simulation Domain Model object
 * properties that are not required to be set.
 * 
 * <p>Used by the model editor app to determine wheter a
 * property is required or not. Apply this annotation to
 * optional property getter methods.   
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
public @interface Optional {

}
