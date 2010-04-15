package cube;

public class SecondaryFace extends Face {

	public SecondaryFace(CornerCubicle corner0, CornerCubicle corner1,
			CornerCubicle corner2, CornerCubicle corner3, EdgeCubicle edge0,
			EdgeCubicle edge1, EdgeCubicle edge2, EdgeCubicle edge3,
			Facelet color) {
		super(corner0, corner1, corner2, corner3, edge0, edge1, edge2, edge3,
				color);

	}

	public SecondaryFace(CornerCubicle[] cornerList, EdgeCubicle[] edgeList,
			Facelet color) throws IllegalArgumentException {
		super(cornerList, edgeList, color);

	}


	/**
	 * The function twists the face clock wise
	 */

	public void cwTwist(){
		super.cwTwist();
		for(int i = 0; i < 4; i++){
			if((int)cornerArray[i].getCornerCubie().getPrimaryDirection() == 0){
				cornerArray[i].getCornerCubie().setPrimaryOrientation((byte)2);

			}else if((int)cornerArray[i].getCornerCubie().getPrimaryDirection() == 2){
				cornerArray[i].getCornerCubie().setPrimaryOrientation((byte)0);
			}

			if((int)edgeArray[i].getEdgeCubie().getPrimaryDirection() == 0){
				edgeArray[i].getEdgeCubie().setPrimaryOrientation((byte)1);

			}else{
				edgeArray[i].getEdgeCubie().setPrimaryOrientation((byte)0);
			}
		}
	}
	/**
	 * The function twists the face counter clock wise
	 */
	public void ccwTwist(){
		super.ccwTwist();
		for(int i = 0; i < 4; i++){
			if((int)cornerArray[i].getCornerCubie().getPrimaryDirection() == 0){
				cornerArray[i].getCornerCubie().setPrimaryOrientation((byte)2);

			}else if((int)cornerArray[i].getCornerCubie().getPrimaryDirection() == 2){
				cornerArray[i].getCornerCubie().setPrimaryOrientation((byte)0);
			}

			if((int)edgeArray[i].getEdgeCubie().getPrimaryDirection() == 0){
				edgeArray[i].getEdgeCubie().setPrimaryOrientation((byte)1);

			}else{
				edgeArray[i].getEdgeCubie().setPrimaryOrientation((byte)0);
			}


		}
	}
}