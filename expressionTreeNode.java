 import java.lang.Math.*;

class expressionTreeNode {
    private String value;
    private expressionTreeNode leftChild, rightChild, parent;
    
    expressionTreeNode() {
        value = null; 
        leftChild = rightChild = parent = null;
    }
    
    // Constructor
    /* Arguments: String s: Value to be stored in the node
                  expressionTreeNode l, r, p: the left child, right child, and parent of the node to created      
       Returns: the newly created expressionTreeNode               
    */
    expressionTreeNode(String s, expressionTreeNode l, expressionTreeNode r, expressionTreeNode p) {
        value = s; 
        leftChild = l; 
        rightChild = r;
        parent = p;
    }
    
    /* Basic access methods */
    String getValue() { return value; }

    expressionTreeNode getLeftChild() { return leftChild; }

    expressionTreeNode getRightChild() { return rightChild; }

    expressionTreeNode getParent() { return parent; }


    /* Basic setting methods */ 
    void setValue(String o) { value = o; }
    
    // sets the left child of this node to n
    void setLeftChild(expressionTreeNode n) { 
        leftChild = n; 
        n.parent = this; 
    }
    
    // sets the right child of this node to n
    void setRightChild(expressionTreeNode n) { 
        rightChild = n; 
        n.parent=this; 
    }
    

    // Returns the root of the tree describing the expression s
    // Watch out: it makes no validity checks whatsoever!
    expressionTreeNode(String s) {
        // check if s contains parentheses. If it doesn't, then it's a leaf
        if (s.indexOf("(")==-1) setValue(s);
        else {  // it's not a leaf

            /* break the string into three parts: the operator, the left operand,
               and the right operand. ***/
            setValue( s.substring( 0 , s.indexOf( "(" ) ) );
            // delimit the left operand 2008
            int left = s.indexOf("(")+1;
            int i = left;
            int parCount = 0;
            // find the comma separating the two operands
            while (parCount>=0 && !(s.charAt(i)==',' && parCount==0)) {
                if ( s.charAt(i) == '(' ) parCount++;
                if ( s.charAt(i) == ')' ) parCount--;
                i++;
            }
            int mid=i;
            if (parCount<0) mid--;

        // recursively build the left subtree
            setLeftChild(new expressionTreeNode(s.substring(left,mid)));
    
            if (parCount==0) {
                // it is a binary operator
                // find the end of the second operand.F13
                while ( ! (s.charAt(i) == ')' && parCount == 0 ) )  {
                    if ( s.charAt(i) == '(' ) parCount++;
                    if ( s.charAt(i) == ')' ) parCount--;
                    i++;
                }
                int right=i;
                setRightChild( new expressionTreeNode( s.substring( mid + 1, right)));
        }
    }
    }


    // Returns a copy of the subtree rooted at this node... 2014
    expressionTreeNode deepCopy() {
        expressionTreeNode n = new expressionTreeNode();
        n.setValue( getValue() );
        if ( getLeftChild()!=null ) n.setLeftChild( getLeftChild().deepCopy() );
        if ( getRightChild()!=null ) n.setRightChild( getRightChild().deepCopy() );
        return n;
    }
    
    // Returns a String describing the subtree rooted at a certain node.
    public String toString() {
        String ret = value;
        if ( getLeftChild() == null ) return ret;
        else ret = ret + "(" + getLeftChild().toString();
        if ( getRightChild() == null ) return ret + ")";
        else ret = ret + "," + getRightChild().toString();
        ret = ret + ")";
        return ret;
    } 


    // Returns the value of the expression rooted at a given node
    // when x has a certain value
    double evaluate(double x) {

        if (getLeftChild()==null) {
            if (getValue().equals("x")) {
                return x;
            }
            else {
                return Double.parseDouble(getValue());
            }
        }
        else {
            if (getRightChild()==null) { //unary operations
                return calc(getValue(), getLeftChild().evaluate(x), 0);
            }
            else { //binary operations
                return calc(getValue(), getLeftChild().evaluate(x), getRightChild().evaluate(x));
            }
        }
    }                                                 

    double calc(String operationType, double leftx, double rightx) {

        if (operationType.equals("add")) {
            return leftx+rightx;
        }
        else if (operationType.equals("minus")) {
            return leftx-rightx;
        }
        else if (operationType.equals("mult")) {
            return leftx*rightx;
        }
        else if (operationType.equals("exp")) {
            return Math.exp(leftx);
        }
        else if (operationType.equals("sin")) {
            return Math.sin(leftx);
        }
        else if (operationType.equals("cos")) {
            return Math.cos(leftx);
        }
        else {
            return 0;
        }
    }

    /* returns the root of a new expression tree representing the derivative of the
       original expression */


    expressionTreeNode differentiate() {                      
        
        expressionTreeNode rC;
        expressionTreeNode lC;

        if (getLeftChild()==null) {
            if (getValue().equals("x")) {
                return new expressionTreeNode("1");
            }
            else {
                return new expressionTreeNode("0");
            }
        }
        else if (getValue().equals("add")) {
            return new expressionTreeNode(getValue(), getLeftChild().differentiate(), getRightChild().differentiate(), getParent());
        }
        else if (getValue().equals("minus")) {
            return new expressionTreeNode(getValue(), getLeftChild().differentiate(), getRightChild().differentiate(), getParent());
        }
        else if (getValue().equals("mult")) {
            rC = new expressionTreeNode(getValue(), getLeftChild().differentiate(), getRightChild().deepCopy(), getParent());
            lC = new expressionTreeNode(getValue(), getLeftChild().deepCopy(), getRightChild().differentiate(), getParent());
            return new expressionTreeNode("add", lC, rC, getParent());
        }
        else if (getValue().equals("exp")) {
            lC = new expressionTreeNode("exp", getLeftChild().deepCopy(), null, getParent());
            rC = getLeftChild().differentiate();
            return new expressionTreeNode("mult", lC, rC, getParent());
        }  
        else if (getValue().equals("sin")) {
            lC = new expressionTreeNode("cos", getLeftChild().deepCopy(), null, getParent());
            rC = getLeftChild().differentiate();
            return new expressionTreeNode("mult", lC, rC, getParent());
        }
        else if (getValue().equals("cos")) {
            lC = new expressionTreeNode("sin", getLeftChild(), null, getParent());
            lC = new expressionTreeNode("minus", new expressionTreeNode("0"), lC, getParent());
            rC = getLeftChild().differentiate();
            return new expressionTreeNode("mult", lC, rC, getParent());
        }  
        return null;
    }
        
    
    public static void main(String args[]) {
        expressionTreeNode e = new expressionTreeNode("mult(mult(2,x),cos(x))");
        System.out.println(e);
        System.out.println(e.evaluate(10));
        System.out.println(e.differentiate());
    }
}
