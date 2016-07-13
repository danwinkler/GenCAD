difference() {
    translate( [0,0,-.3] ) {
        include <test.scad>; 
    };
    translate( [0, 0, -100] ) {
        cylinder( h=100, r=100 );
    };
}