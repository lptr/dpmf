	/* pathfinder */
	public LinkedList PathFinder(Coordinates start, Coordinates stop) {
		myset = new TreeSet(new PathComparator());
		myset.add(new PathItem(start,start.getDistance(stop),0),null);

		clearBlocked();
		PathItem min;
//		while (!(min=myset.first()).getPos().equals(stop)) { // ha a min null akkor lefagy
		while (((min=myset.first())!=null) && (!(min=myset.first()).getPos().SameField(stop)))
			myset.remove(min);
			if (isNeighbour(min,Coordinates.DIR_UP,false)) {
				Coordinates temp = min.getNeighbour(Coordinates.DIR_UP);
				if (!getFieldAt(temp).getSetBlocked())
					myset.add(new PathItem(temp,temp.getDistance(stop),min.getDist()+1,min));
			}
			if (isNeighbour(min,Coordinates.DIR_DOWN,false)) {
				Coordinates temp = min.getNeighbour(Coordinates.DIR_DOWN);
				if (!getFieldAt(temp).getSetBlocked())
					myset.add(new PathItem(temp,temp.getDistance(stop),min.getDist()+1,min));
			}
			if (isNeighbour(min,Coordinates.DIR_LEFT,false)) {
				Coordinates temp = min.getNeighbour(Coordinates.DIR_LEFT);
				if (!getFieldAt(temp).getSetBlocked())
					myset.add(new PathItem(temp,temp.getDistance(stop),min.getDist()+1,min));
			}
			if (isNeighbour(min,Coordinates.DIR_RIGHT,false)) {
				Coordinates temp = min.getNeighbour(Coordinates.DIR_RIGHT);
				if (!getFieldAt(temp).getSetBlocked())
					myset.add(new PathItem(temp,temp.getDistance(stop),min.getDist()+1,min));
			}
		}

		while (!(min=myset.first()).getPos().SameField(start)) {
			min.getParent().setChild(min);
			min = min.getParent();
		}

//		return min;
		LinkedList retValue = new LinkedList(); 

		while (min!=null) {
			retValue.add(new Coordinates(min.getX(),min.getY()));
			min = min.getChild();
		}

		return retValue;
	}

--------------------------


	public FieldItem getFieldAt(Coordinates pos) {
		return mazeData[mazeWidth*pos.getY() + pos.getX()];
	}
